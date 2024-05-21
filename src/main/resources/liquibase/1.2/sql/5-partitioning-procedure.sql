CREATE OR REPLACE PROCEDURE payment_api_app.create_partitions(
    in start_date timestamp,
    in in_table_name character varying,
    IN in_partitions_count numeric
)
    LANGUAGE plpgsql
as
$BODY$
declare
    from_date      timestamp;
    to_date        timestamp;
    day            varchar;
    month          varchar;
    year           varchar;
    partition_name varchar;
begin
    from_date := start_date;
    for i in 1..in_partitions_count
        loop
            select (from_date + interval '1 days') into from_date;
            select (from_date + interval '1 days') into to_date;

            select LPAD(extract('day' from from_date)::varchar, 2, '0') into day;
            select LPAD(extract('month' from from_date)::varchar, 2, '0') into month;
            select (extract('year' from from_date))::varchar into year;

            select in_table_name || '_' || year || month || day into partition_name;

            execute format(
                    'create table if not exists payment_api_app.%I (like payment_api_app.%I including defaults including constraints);',
                    partition_name, in_table_name);
            if not exists(select null
                          from pg_catalog.pg_inherits i
                                   join pg_catalog.pg_class as c on c.oid = i.inhrelid
                          where c.relname = partition_name) then
                execute format(
                        'alter table payment_api_app.%I attach partition payment_api_app.%I for values from (%L) to (%L);',
                        in_table_name, partition_name, from_date, to_date);
            end if;
        end loop;
end;
$BODY$;
