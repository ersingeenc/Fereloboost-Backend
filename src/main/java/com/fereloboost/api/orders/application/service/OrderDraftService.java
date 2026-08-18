package com.fereloboost.api.orders.application.service;

import com.fereloboost.api.catalog.application.service.CatalogQueryService;
import com.fereloboost.api.catalog.domain.model.CatalogService;
import com.fereloboost.api.identity.application.service.UserNotFoundException;
import com.fereloboost.api.identity.domain.repository.UserRepository;
import com.fereloboost.api.orders.api.request.SaveOrderDraftRequest;
import com.fereloboost.api.orders.api.response.OrderAddonResponse;
import com.fereloboost.api.orders.api.response.OrderDraftResponse;
import com.fereloboost.api.orders.api.response.OrderSummaryResponse;
import com.fereloboost.api.orders.domain.model.CustomerOrder;
import com.fereloboost.api.orders.domain.model.CustomerOrderAddon;
import com.fereloboost.api.orders.domain.model.CustomerOrderEvent;
import com.fereloboost.api.orders.domain.model.OrderStatus;
import com.fereloboost.api.orders.infrastructure.persistence.CustomerOrderAddonRepository;
import com.fereloboost.api.orders.infrastructure.persistence.CustomerOrderEventRepository;
import com.fereloboost.api.orders.infrastructure.persistence.CustomerOrderRepository;
import com.fereloboost.api.pricing.infrastructure.persistence.ServiceAddonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderDraftService {

    private final CatalogQueryService catalogQueryService;
    private final UserRepository userRepository;
    private final ServiceAddonRepository serviceAddonRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderAddonRepository customerOrderAddonRepository;
    private final CustomerOrderEventRepository customerOrderEventRepository;

    @Transactional
    public OrderDraftResponse createDraft(UUID customerId, SaveOrderDraftRequest request) {
        var customer = userRepository.findById(customerId).orElseThrow(UserNotFoundException::new);
        var service = catalogQueryService.findActiveService(request.serviceCode());
        var configuration = validateConfiguration(service, request.configuration());
        var addons = validateAddons(service, request.addonCodes());

        var order = customerOrderRepository.save(CustomerOrder.createDraft(customer, service, configuration));
        customerOrderAddonRepository.saveAll(addons.stream().map(addon -> CustomerOrderAddon.snapshot(order, addon)).toList());
        customerOrderEventRepository.save(CustomerOrderEvent.draftCreated(order, customer));
        return toDraftResponse(order);
    }

    @Transactional
    public OrderDraftResponse updateDraft(UUID customerId, UUID orderId, SaveOrderDraftRequest request) {
        var order = findOwnedOrder(customerId, orderId);
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu sipariş artık taslak olarak düzenlenemez.");
        }
        var requestedServiceCode = normalize(request.serviceCode());
        if (!order.getServiceCode().equals(requestedServiceCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Taslak üzerinde hizmet değiştirilemez. Farklı bir hizmet için yeni taslak oluştur.");
        }
        var configuration = validateConfiguration(order.getService(), request.configuration());
        var addons = validateAddons(order.getService(), request.addonCodes());
        order.updateDraft(configuration);
        customerOrderAddonRepository.deleteAllByOrderId(order.getId());
        customerOrderAddonRepository.saveAll(addons.stream().map(addon -> CustomerOrderAddon.snapshot(order, addon)).toList());
        customerOrderEventRepository.save(CustomerOrderEvent.draftUpdated(order,
                userRepository.findById(customerId).orElseThrow(UserNotFoundException::new)));
        return toDraftResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> listOrders(UUID customerId) {
        return customerOrderRepository.findAllByCustomerIdOrderByUpdatedAtDesc(customerId).stream()
                .map(OrderSummaryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderDraftResponse getOrder(UUID customerId, UUID orderId) {
        return toDraftResponse(findOwnedOrder(customerId, orderId));
    }

    private CustomerOrder findOwnedOrder(UUID customerId, UUID orderId) {
        return customerOrderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı."));
    }

    private List<com.fereloboost.api.pricing.domain.model.ServiceAddon> validateAddons(CatalogService service,
                                                                                          List<String> requestedCodes) {
        var codes = requestedCodes.stream().map(this::normalize).collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        if (codes.isEmpty()) {
            return List.of();
        }
        var addons = serviceAddonRepository.findAllByServiceIdAndCodeInAndActiveTrue(service.getId(), List.copyOf(codes));
        if (addons.size() != codes.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seçilen ek hizmetlerden biri bu hizmet için geçerli değil.");
        }
        return addons;
    }

    private Map<String, Object> validateConfiguration(CatalogService service, Map<String, Object> requestConfiguration) {
        Set<String> allowedFields = requiredFields(service);
        if (!requestConfiguration.keySet().equals(allowedFields)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sipariş yapılandırması, hizmetin beklediği alanlarla tam olarak eşleşmelidir.");
        }

        var sanitized = new LinkedHashMap<String, Object>();
        for (String field : allowedFields) {
            Object value = requestConfiguration.get(field);
            if (value == null || value instanceof Collection<?> || value instanceof Map<?, ?> || value.getClass().isArray()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'" + field + "' alanı geçersiz.");
            }
            if (!(value instanceof String || value instanceof Number || value instanceof Boolean)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'" + field + "' alanı desteklenmeyen bir değer içeriyor.");
            }
            if (value instanceof String text && (text.isBlank() || text.length() > 120)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'" + field + "' alanı 1-120 karakter arasında olmalı.");
            }
            sanitized.put(field, value);
        }
        return Map.copyOf(sanitized);
    }

    private Set<String> requiredFields(CatalogService service) {
        Object configuredFields = service.getConfigurationSchema().get("fields");
        if (!(configuredFields instanceof Collection<?> fields) || fields.isEmpty()) {
            throw new IllegalStateException("Hizmet yapılandırma şeması geçersiz.");
        }
        var result = new LinkedHashSet<String>();
        for (Object field : fields) {
            if (!(field instanceof String name) || name.isBlank()) {
                throw new IllegalStateException("Hizmet yapılandırma şeması geçersiz.");
            }
            result.add(name);
        }
        return Set.copyOf(result);
    }

    private OrderDraftResponse toDraftResponse(CustomerOrder order) {
        var addons = customerOrderAddonRepository.findAllByOrderIdOrderByCreatedAtAsc(order.getId()).stream()
                .map(OrderAddonResponse::from).toList();
        return OrderDraftResponse.from(order, addons);
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
