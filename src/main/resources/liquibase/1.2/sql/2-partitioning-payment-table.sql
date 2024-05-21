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

        execute concat('alter table payment_api_app.payment add constraint payment_old_records
            check (date_created < to_date(''2024-05-01'', ''yyyy-mm-dd'')) not valid;');
        commit;

        alter table payment_api_app.payment
            validate constraint payment_old_records;
        commit;

        alter table payment_api_app.payment
            rename to payment_old;
        alter index payment_api_app.payment_api_app_payment_pk rename to payment_api_app_payment_pk_old;
        alter index payment_api_app.index_payment_date_created rename to index_payment_date_created_old;

        create table if not exists payment_api_app.payment
        (
            ID              NUMERIC        not null,
            DATE_CREATED    TIMESTAMP      not null,
            DATE_UPDATED    TIMESTAMP      not null,
            AMOUNT          NUMERIC(18, 2) not null,
            ORIGINAL_AMOUNT NUMERIC(18, 2) not null,
            STATUS          VARCHAR(50)    not null,
            ADDITIONAL_DATA JSONB
        ) partition by range (date_created);

        create table if not exists payment_api_app.payment_20240501
        (
            like payment_api_app.payment including all
        );
        execute concat('alter table payment_api_app.payment attach partition payment_api_app.payment_20240501 for values from (''2024-05-01'') to (''2024-05-02'');');
        execute concat('alter table payment_api_app.payment attach partition payment_api_app.payment_old for values from (''2024-04-01'') to (''2024-05-01'');');
        commit;

        alter table payment_api_app.payment_old
            drop constraint payment_old_records;
        commit;
    end
$BODY$;

create index if not exists index_payment_id
    on only payment_api_app.payment (id);
create index if not exists index_payment_date_created
    on only payment_api_app.payment (date_created);

create index if not exists index_payment_20240501_id
    on payment_api_app.payment_20240501 (id);
create index if not exists index_payment_20240501_date_created
    on payment_api_app.payment_20240501 (date_created);

alter index payment_api_app.index_payment_id
    attach partition payment_api_app.index_payment_20240501_id;
alter index payment_api_app.index_payment_date_created
    attach partition payment_api_app.index_payment_20240501_date_created;