create table if not exists payment_api_app.request
(
    ID               NUMERIC        not null
        constraint PAYMENT_API_APP_REQUEST_PK primary key,
    DATE_CREATED     TIMESTAMP      not null,
    DATE_UPDATED     TIMESTAMP      not null,
    REQUEST_TYPE     VARCHAR(50)    not null,
    AMOUNT           NUMERIC(18, 2) not null,
    STATUS           VARCHAR(50)    not null,
    TERMINAL_ID      VARCHAR(20)    not null,
    MESSAGE          VARCHAR(256),
    EXTENSION_FIELDS JSONB,
    PAYMENT_ID       NUMERIC        not null
);

create table if not exists payment_api_app.payment
(
    ID              NUMERIC        not null
        constraint PAYMENT_API_APP_PAYMENT_PK primary key,
    DATE_CREATED    TIMESTAMP      not null,
    DATE_UPDATED    TIMESTAMP      not null,
    AMOUNT          NUMERIC(18, 2) not null,
    ORIGINAL_AMOUNT NUMERIC(18, 2) not null,
    STATUS          VARCHAR(50)    not null,
    ADDITIONAL_DATA JSONB
);

create table if not exists payment_api_app.card_data
(
    ID              NUMERIC     not null
        constraint PAYMENT_API_APP_CARD_DATA_PK primary key,
    DATE_CREATED    TIMESTAMP   not null,
    DATE_UPDATED    TIMESTAMP   not null,
    PAYMENT_SCHEME  VARCHAR(50) not null,
    PAN             VARCHAR(50),
    PAN_EXP_DATE    VARCHAR(24),
    CARDHOLDER_NAME VARCHAR(50),
    PAYMENT_ID      NUMERIC
);

create table if not exists payment_api_app.nspk_data
(
    ID               NUMERIC   not null
        constraint PAYMENT_API_APP_NSPK_DATA_PK primary key,
    DATE_CREATED     TIMESTAMP not null,
    DATE_UPDATED     TIMESTAMP not null,
    RESPONSE_CODE    VARCHAR(50),
    ERROR_CODE       VARCHAR(30),
    ERROR_MESSAGE    VARCHAR(50),
    REQUEST_MESSAGE  JSONB,
    RESPONSE_MESSAGE JSONB,
    PAYMENT_ID       NUMERIC   not null,
    REQUEST_ID       NUMERIC   not null
);

CREATE SEQUENCE if not exists payment_api_app.REQUEST_SEQ
    START WITH 1;

CREATE SEQUENCE if not exists payment_api_app.PAYMENT_SEQ
    START WITH 1;

CREATE SEQUENCE if not exists payment_api_app.CARD_DATA_SEQ
    START WITH 1;

CREATE SEQUENCE if not exists payment_api_app.NSPK_DATA_SEQ
    START WITH 1;