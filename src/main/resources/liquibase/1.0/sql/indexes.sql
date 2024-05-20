create index if not exists index_request_payment_id
    on payment_api_app.request (PAYMENT_ID);
create index if not exists index_request_date_created
    on payment_api_app.request (DATE_CREATED);
create index if not exists index_payment_date_created
    on payment_api_app.payment (DATE_CREATED);
create index if not exists index_card_data_payment_id
    on payment_api_app.card_data (PAYMENT_ID);
create index if not exists index_card_data_date_created
    on payment_api_app.card_data (DATE_CREATED);
create index if not exists index_nspk_data_request_id
    on payment_api_app.nspk_data (REQUEST_ID);
create index if not exists index_nspk_data_payment_id
    on payment_api_app.nspk_data (PAYMENT_ID);
create index if not exists index_nspk_data_date_created
    on payment_api_app.nspk_data (DATE_CREATED);