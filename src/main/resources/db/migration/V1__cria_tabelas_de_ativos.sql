-- Schema do catalogo de ativos: crypto (CoinGecko) e stock (Alpaca).
--
-- IF NOT EXISTS porque bancos que ja rodaram com ddl-auto: update tem as
-- tabelas criadas pelo Hibernate e nenhum historico do Flyway. O
-- spring.flyway.baseline-on-migrate (application.yaml) registra esse estado
-- no 0 em vez de pular esta migracao.

CREATE TABLE IF NOT EXISTS crypto (
    id       uuid NOT NULL,
    asset_id varchar(255),
    name     varchar(255),
    symbol   varchar(255),
    active   boolean,
    CONSTRAINT pk_crypto PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS stock (
    id       uuid NOT NULL,
    symbol   varchar(255),
    name     varchar(255),
    exchange varchar(255),
    active   varchar(255),
    CONSTRAINT pk_stock PRIMARY KEY (id),
    CONSTRAINT uk_stock_symbol UNIQUE (symbol)
);
