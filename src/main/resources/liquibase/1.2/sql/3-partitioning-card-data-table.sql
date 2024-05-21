do
$BODY$
    declare
        autovacuum_pid integer;
    begin
        set maintenance_work_mem = '1024MB';

        select (select pid
                from pg_catalog.pg_stat_activity sa
                where query like 'autovacuum:%')
        into autovacuum_pid;
        if (autovacuum_pid is not null) then
            perform pg_terminate_backend(autovacuum_pid);
        end if;

        execute concat('alter table payment_api_app.card_data add constraint card_data_old_records
            check (date_created < to_date(''2024-05-01'', ''yyyy-mm-dd'')) not valid;');
        commit;

        alter table payment_api_app.card_data
            validate constraint card_data_old_records;
        commit;

        alter table payment_api_app.card_data
            rename to card_data_old;
        alter index payment_api_app.payment_api_app_card_data_pk rename to payment_api_app_card_data_pk_old;
        alter index payment_api_app.index_card_data_payment_id rename to index_card_data_payment_id_old;
        alter index payment_api_app.index_card_data_date_created rename to index_card_data_date_created_old;

        create table if not exists payment_api_app.card_data
        (
            ID              NUMERIC     not null,
            DATE_CREATED    TIMESTAMP   not null,
            DATE_UPDATED    TIMESTAMP   not null,
            PAYMENT_SCHEME  VARCHAR(50) not null,
            PAN             VARCHAR(50),
            PAN_EXP_DATE    VARCHAR(24),
            CARDHOLDER_NAME VARCHAR(50),
            PAYMENT_ID      NUMERIC
        ) partition by range (date_created);

        create table if not exists payment_api_app.card_data_20240501
        (
            like payment_api_app.card_data including all
        );
        execute concat('alter table payment_api_app.card_data attach partition payment_api_app.card_data_20240501 for values from (''2024-05-01'') to (''2024-05-02'');');
        execute concat('alter table payment_api_app.card_data attach partition payment_api_app.card_data_old for values from (''2024-04-01'') to (''2024-05-01'');');
        commit;

        alter table payment_api_app.card_data_old
            drop constraint card_data_old_records;
        commit;
    end
$BODY$;

create index if not exists index_card_data_id
    on only payment_api_app.card_data (id);
create index if not exists index_card_data_payment_id
    on only payment_api_app.card_data (payment_id);
create index if not exists index_card_data_date_created
    on only payment_api_app.card_data (date_created);

create index if not exists index_card_data_20240501_id
    on payment_api_app.card_data_20240501 (id);
create index if not exists index_card_data_20240501_payment_id
    on payment_api_app.card_data_20240501 (payment_id);
create index if not exists index_card_data_20240501_date_created
    on payment_api_app.card_data_20240501 (date_created);

alter index payment_api_app.index_card_data_id
    attach partition payment_api_app.index_card_data_20240501_id;
alter index payment_api_app.index_card_data_payment_id
    attach partition payment_api_app.index_card_data_20240501_payment_id;
alter index payment_api_app.index_card_data_date_created
    attach partition payment_api_app.index_card_data_20240501_date_created;