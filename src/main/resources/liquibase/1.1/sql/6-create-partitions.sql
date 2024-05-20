call payment_api_app.create_partitions(timestamp '2024-05-01 00:00:00', 'payment', 30);
call payment_api_app.create_partitions(timestamp '2024-05-01 00:00:00', 'request', 30);
call payment_api_app.create_partitions(timestamp '2024-05-01 00:00:00', 'card_data', 30);
call payment_api_app.create_partitions(timestamp '2024-05-01 00:00:00', 'nspk_data', 30);
