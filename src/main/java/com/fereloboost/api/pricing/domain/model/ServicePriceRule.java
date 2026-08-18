package com.fereloboost.api.pricing.domain.model;

import com.fereloboost.api.catalog.domain.model.CatalogService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

@Getter
@Entity
@Table(name = "service_price_rules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServicePriceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private CatalogService service;

    @Column(name = "from_rank_code", nullable = false, length = 80)
    private String fromRankCode;

    @Column(name = "to_rank_code", nullable = false, length = 80)
    private String toRankCode;

    @Column(name = "base_price_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePriceAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private boolean active;

    private ServicePriceRule(CatalogService service, String fromRankCode, String toRankCode,
                             BigDecimal basePriceAmount, String currency) {
        this.service = service;
        this.fromRankCode = fromRankCode;
        this.toRankCode = toRankCode;
        this.basePriceAmount = basePriceAmount;
        this.currency = currency;
        this.active = true;
    }

    public static ServicePriceRule create(CatalogService service, String fromRankCode, String toRankCode,
                                          BigDecimal basePriceAmount, String currency) {
        return new ServicePriceRule(service, fromRankCode, toRankCode, basePriceAmount, currency);
    }

    public void updatePrice(BigDecimal basePriceAmount, String currency) {
        this.basePriceAmount = basePriceAmount;
        this.currency = currency.toUpperCase(Locale.ROOT);
        this.active = true;
    }
}
