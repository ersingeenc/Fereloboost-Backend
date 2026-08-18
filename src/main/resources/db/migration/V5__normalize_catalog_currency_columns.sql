ALTER TABLE service_addons
    ALTER COLUMN currency TYPE VARCHAR(3);

ALTER TABLE service_price_rules
    ALTER COLUMN currency TYPE VARCHAR(3);
