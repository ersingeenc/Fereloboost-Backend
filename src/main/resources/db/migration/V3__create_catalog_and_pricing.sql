CREATE TABLE games (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(40) NOT NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    sort_order SMALLINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_games_code UNIQUE (code),
    CONSTRAINT uk_games_slug UNIQUE (slug)
);

CREATE TABLE catalog_services (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    game_id UUID NOT NULL REFERENCES games (id) ON DELETE RESTRICT,
    code VARCHAR(80) NOT NULL,
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    pricing_model VARCHAR(30) NOT NULL,
    configuration_schema JSONB NOT NULL DEFAULT '{}'::jsonb,
    sort_order SMALLINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_catalog_services_code UNIQUE (code),
    CONSTRAINT uk_catalog_services_game_slug UNIQUE (game_id, slug)
);

CREATE TABLE service_addons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_id UUID NOT NULL REFERENCES catalog_services (id) ON DELETE CASCADE,
    code VARCHAR(80) NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(250),
    price_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    currency CHAR(3) NOT NULL DEFAULT 'TRY',
    sort_order SMALLINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_service_addons_service_code UNIQUE (service_id, code),
    CONSTRAINT ck_service_addons_price_non_negative CHECK (price_amount >= 0)
);

CREATE TABLE service_price_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_id UUID NOT NULL REFERENCES catalog_services (id) ON DELETE CASCADE,
    from_rank_code VARCHAR(80) NOT NULL,
    to_rank_code VARCHAR(80) NOT NULL,
    base_price_amount NUMERIC(12, 2) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'TRY',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_service_price_rules_path UNIQUE (service_id, from_rank_code, to_rank_code),
    CONSTRAINT ck_service_price_rules_price_non_negative CHECK (base_price_amount >= 0),
    CONSTRAINT ck_service_price_rules_different_ranks CHECK (from_rank_code <> to_rank_code)
);

CREATE INDEX idx_catalog_services_game_active ON catalog_services (game_id, active, sort_order);
CREATE INDEX idx_service_addons_service_active ON service_addons (service_id, active, sort_order);
CREATE INDEX idx_service_price_rules_lookup ON service_price_rules (service_id, from_rank_code, to_rank_code) WHERE active;

INSERT INTO games (code, name, slug, description, sort_order) VALUES
    ('LEAGUE_OF_LEGENDS', 'League of Legends', 'league-of-legends', 'League of Legends dereceli oyun hizmetleri.', 10),
    ('VALORANT', 'Valorant', 'valorant', 'Valorant dereceli oyun hizmetleri.', 20),
    ('TEAMFIGHT_TACTICS', 'Teamfight Tactics', 'teamfight-tactics', 'Teamfight Tactics dereceli oyun hizmetleri.', 30);

INSERT INTO catalog_services (game_id, code, name, slug, description, pricing_model, configuration_schema, sort_order)
SELECT g.id, service.code, service.name, service.slug, service.description, service.pricing_model, service.configuration_schema::jsonb, service.sort_order
FROM games g
JOIN (VALUES
    ('LEAGUE_OF_LEGENDS', 'LOL_ELO_BOOST', 'Eloboost', 'eloboost', 'Mevcut liginden hedef ligine profesyonel destek.', 'RANK_PATH', '{"fields":["currentRank","currentLp","targetRank","queueType","role","server"]}', 10),
    ('LEAGUE_OF_LEGENDS', 'LOL_DUO_BOOST', 'Duoboost', 'duoboost', 'Bir booster ile birlikte oynayarak rank atla.', 'RANK_PATH', '{"fields":["currentRank","currentLp","targetRank","role","server"]}', 20),
    ('LEAGUE_OF_LEGENDS', 'LOL_WIN_BOOST', 'Winboost', 'winboost', 'İhtiyacın olan galibiyet sayısı için destek.', 'PER_WIN', '{"fields":["currentRank","winCount","queueType","server"]}', 30),
    ('LEAGUE_OF_LEGENDS', 'LOL_PLACEMENT_MATCHES', 'İlk 10 Maç', 'ilk-10-mac', 'Yerleştirme maçları için profesyonel destek.', 'PER_MATCH', '{"fields":["previousRank","queueType","server"]}', 40),
    ('LEAGUE_OF_LEGENDS', 'LOL_CHALLENGER_BOOST', 'Challenger', 'challenger', 'Challenger hedefi için özel değerlendirmeli hizmet.', 'CUSTOM', '{"fields":["currentRank","targetRank","server"]}', 50),
    ('LEAGUE_OF_LEGENDS', 'LOL_COACHING', 'Koçluk', 'kocluk', 'Bire bir oyun koçluğu.', 'HOURLY', '{"fields":["hours","role","server"]}', 60),
    ('VALORANT', 'VALORANT_ELO_BOOST', 'Eloboost', 'eloboost', 'Mevcut rankından hedef rankına yüksel.', 'RANK_PATH', '{"fields":["currentRank","currentRr","targetRank","server"]}', 10),
    ('VALORANT', 'VALORANT_DUO_BOOST', 'Duoboost', 'duoboost', 'Bir booster ile birlikte oynayarak rank atla.', 'RANK_PATH', '{"fields":["currentRank","currentRr","targetRank","server"]}', 20),
    ('VALORANT', 'VALORANT_RADIANT_BOOST', 'Radiant Boost', 'radiant-boost', 'Radiant hedefi için özel değerlendirmeli hizmet.', 'CUSTOM', '{"fields":["currentRank","targetRank","server"]}', 30),
    ('VALORANT', 'VALORANT_PLACEMENT', 'Yerleştirme', 'yerlestirme', 'Yerleştirme maçları için profesyonel destek.', 'PER_MATCH', '{"fields":["previousRank","server"]}', 40),
    ('VALORANT', 'VALORANT_WIN_BOOST', 'Win Boost', 'win-boost', 'İhtiyacın olan galibiyet sayısı için destek.', 'PER_WIN', '{"fields":["currentRank","winCount","server"]}', 50),
    ('VALORANT', 'VALORANT_COACHING', 'Koçluk', 'kocluk', 'Bire bir oyun koçluğu.', 'HOURLY', '{"fields":["hours","server"]}', 60),
    ('TEAMFIGHT_TACTICS', 'TFT_ELO_BOOST', 'Eloboost', 'eloboost', 'TFT rank hedefin için profesyonel destek.', 'RANK_PATH', '{"fields":["currentRank","currentLp","targetRank","server","role"]}', 10),
    ('TEAMFIGHT_TACTICS', 'TFT_WIN_BOOST', 'Winboost', 'winboost', 'İhtiyacın olan galibiyet sayısı için destek.', 'PER_WIN', '{"fields":["currentRank","winCount","server"]}', 20),
    ('TEAMFIGHT_TACTICS', 'TFT_PLACEMENT', 'Yerleştirme Maçları', 'yerlestirme-maclari', 'Yerleştirme maçları için profesyonel destek.', 'PER_MATCH', '{"fields":["previousRank","server"]}', 30)
) AS service(game_code, code, name, slug, description, pricing_model, configuration_schema, sort_order)
ON g.code = service.game_code;

INSERT INTO service_addons (service_id, code, name, description, price_amount, sort_order)
SELECT cs.id, addon.code, addon.name, addon.description, addon.price_amount, addon.sort_order
FROM catalog_services cs
JOIN (VALUES
    ('LOL_ELO_BOOST', 'FLASH_D', 'Flash D', 'Summoner Flash D seçeneği.', 0, 10),
    ('LOL_ELO_BOOST', 'FLASH_F', 'Flash F', 'Summoner Flash F seçeneği.', 0, 20),
    ('LOL_ELO_BOOST', 'OFFLINE_MODE', 'Çevrimdışı Mod', 'Booster işlemi görünmez modda yapar.', 50, 30),
    ('LOL_ELO_BOOST', 'LIVE_STREAM', 'Canlı Yayın', 'İşlem canlı yayın bağlantısıyla takip edilir.', 140, 40),
    ('VALORANT_ELO_BOOST', 'LIVE_STREAM', 'Canlı Yayın', 'İşlem canlı yayın bağlantısıyla takip edilir.', 140, 10),
    ('TFT_ELO_BOOST', 'OFFLINE_MODE', 'Çevrimdışı Mod', 'Booster işlemi görünmez modda yapar.', 50, 10),
    ('TFT_ELO_BOOST', 'LIVE_STREAM', 'Canlı Yayın', 'İşlem canlı yayın bağlantısıyla takip edilir.', 140, 20)
) AS addon(service_code, code, name, description, price_amount, sort_order)
ON cs.code = addon.service_code;
