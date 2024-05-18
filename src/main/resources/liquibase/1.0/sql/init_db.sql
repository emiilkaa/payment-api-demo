create table PAYMENT_API_APP.REQUEST
(
    ID               NUMBER not null
        constraint PAYMENT_API_APP_REQUEST_PK primary key,
    DATE_CREATED     TIMESTAMP(6),
    DATE_UPDATED     TIMESTAMP(6),
    REQUEST_TYPE     VARCHAR2(50) not null,
    AMOUNT           NUMBER not null,
    STATUS           VARCHAR2(50) not null,
    TERMINAL_ID      VARCHAR2(20) not null,
    MESSAGE          VARCHAR2(256),
    EXTENSION_FIELDS CLOB,
    PAYMENT_ID       NUMBER not null
);

create table PAYMENT_API_APP.PAYMENT
(
    ID               NUMBER not null
        constraint PAYMENT_API_APP_PAYMENT_PK primary key,
    DATE_CREATED     TIMESTAMP(6),
    DATE_UPDATED     TIMESTAMP(6),
    AMOUNT           NUMBER not null,
    ORIGINAL_AMOUNT           NUMBER not null,
    STATUS           VARCHAR2(50) not null,
    ADDITIONAL_DATA CLOB
);

create table PAYMENT_API_APP.CARD_DATA
(
    ID               NUMBER not null
        constraint PAYMENT_API_APP_CARD_DATA_PK primary key,
    DATE_CREATED     TIMESTAMP(6),
    DATE_UPDATED     TIMESTAMP(6),
    PAYMENT_SCHEME           VARCHAR2(50) not null,
    PAN           VARCHAR2(50),
    PAN_EXP_DATE           VARCHAR2(24),
    CARDHOLDER_NAME VARCHAR2(50),
    PAYMENT_ID       NUMBER
);

create table PAYMENT_API_APP.NSPK_DATA
(
    ID               NUMBER not null
        constraint PAYMENT_API_APP_NSPK_DATA_PK primary key,
    DATE_CREATED     TIMESTAMP(6),
    DATE_UPDATED     TIMESTAMP(6),
    RESPONSE_CODE VARCHAR2(50),
    ERROR_CODE VARCHAR2(30),
    ERROR_MESSAGE VARCHAR2(50),
    REQUEST_MESSAGE CLOB,
    RESPONSE_MESSAGE CLOB,
    PAYMENT_ID       NUMBER not null,
    REQUEST_ID       NUMBER not null
);

CREATE SEQUENCE PAYMENT_API_APP.REQUEST_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE PAYMENT_API_APP.PAYMENT_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE PAYMENT_API_APP.CARD_DATA_SEQ
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE PAYMENT_API_APP.NSPK_DATA_SEQ
    START WITH 1
    INCREMENT BY 1;

GRANT SELECT, UPDATE, INSERT on PAYMENT_API_APP.REQUEST to PAYMENT_API_APP;
GRANT SELECT, UPDATE, INSERT on PAYMENT_API_APP.PAYMENT to PAYMENT_API_APP;
GRANT SELECT, UPDATE, INSERT on PAYMENT_API_APP.CARD_DATA to PAYMENT_API_APP;
GRANT SELECT, UPDATE, INSERT on PAYMENT_API_APP.NSPK_DATA to PAYMENT_API_APP;

GRANT SELECT on PAYMENT_API_APP.REQUEST_SEQ to PAYMENT_API_APP;
GRANT SELECT on PAYMENT_API_APP.PAYMENT_SEQ to PAYMENT_API_APP;
GRANT SELECT on PAYMENT_API_APP.CARD_DATA_SEQ to PAYMENT_API_APP;
GRANT SELECT on PAYMENT_API_APP.NSPK_DATA_SEQ to PAYMENT_API_APP;