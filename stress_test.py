import asyncio
import random
import string
from datetime import datetime, timedelta
from random import randrange

import aiohttp

payments = {}
MAX_CONCURRENT_TASKS = 100
semaphore = asyncio.Semaphore(MAX_CONCURRENT_TASKS)


def random_date(start, end):
    delta = end - start
    int_delta = (delta.days * 24 * 60 * 60) + delta.seconds
    random_second = randrange(int_delta)
    return start + timedelta(seconds=random_second)


async def execute_call(session):
    global payments
    async with (semaphore):
        try:
            urls = ['http://77.232.142.33:8080/api/v1/operations/pay',
                    'http://77.232.142.33:8080/api/v1/operations/cancel',
                    'http://77.232.142.33:8080/api/v1/operation-info/request/',
                    'http://77.232.142.33:8080/api/v1/operation-info/payment/']

            start_date = datetime.strptime('2024-04-20T10:00:00', '%Y-%m-%dT%H:%M:%S')
            end_date = datetime.strptime('2024-05-19T22:00:00', '%Y-%m-%dT%H:%M:%S')
            years = list(range(25, 34))
            months = list(range(1, 13))
            operation = random.choice(list(range(len(urls))))
            if operation == 0:
                amount = random.uniform(100, 10000)
                terminal_id = ''.join(random.choices(string.digits, k=5))
                operation_date = random_date(start_date, end_date).strftime('%Y-%m-%dT%H:%M:%S')
                card_number = ''.join(random.choices(string.digits, k=16))
                cardholder_len = random.randint(5, 10)
                request_body = {'amount': f'{amount:.2f}', 'terminalId': terminal_id, 'operationDate': operation_date,
                                'cardInfo': {'cardNumber': card_number,
                                             'expDate': '{:02d}{}'.format(random.choice(months), random.choice(years)),
                                             'cardHolderName': ''.join(
                                                 random.choices(string.ascii_uppercase, k=cardholder_len))},
                                'extension': {'country': 'Russia', 'currency': 'RUB', 'city': 'Moscow',
                                              'operationCode': ''.join(random.choices(string.digits, k=4))},
                                'requestType': 'PAY'}
            elif operation == 1:
                if not payments:
                    print('No payments to cancel')
                    return
                original_request_id, payment_info = random.choice(list(payments.items()))
                amount = random.uniform(1, payment_info['amount'])
                operation_date = random_date(datetime.strptime(payment_info['operationDate'], '%Y-%m-%dT%H:%M:%S'),
                                             end_date).strftime('%Y-%m-%dT%H:%M:%S')
                request_body = {'originalRequestId': original_request_id, 'amount': f'{amount:.2f}',
                                'terminalId': payment_info['terminalId'], 'operationDate': operation_date,
                                'extension': {'country': 'Russia', 'currency': 'RUB', 'city': 'Moscow',
                                              'operationCode': ''.join(random.choices(string.digits, k=4)),
                                              'autoCancel': random.choice(['0', '1'])}, 'requestType': 'CANCEL'}

            if 0 <= operation <= 1:
                async with session.post(urls[operation], json=request_body, timeout=60) as response:
                    response_json = await response.json()
                    print(f'Operation: {operation}\tRequest: {request_body}\tResponse: {response_json}')
                    if operation == 0 and response_json['status'] == 'SUCCESS':
                        payments[response_json['requestId']] = {'amount': amount, 'operationDate': operation_date,
                                                                'terminalId': terminal_id}
                    if operation == 1 and response_json['status'] == 'SUCCESS':
                        payments[original_request_id]['amount'] -= amount
                        if payments[original_request_id]['amount'] <= 0:
                            del payments[original_request_id]
            else:
                request_id = random.choice(list(range(1, 10001)))
                async with session.get(urls[operation] + str(request_id), timeout=60) as response:
                    response_json = await response.json()
                    print(f'Operation: {operation}\tID: {request_id}\tResponse: {response_json}')
        except Exception as e:
            print(f'Error: {e}')


async def load_test_api():
    async with aiohttp.ClientSession() as session:
        tasks = set()
        while True:
            task = asyncio.create_task(execute_call(session))
            tasks.add(task)

            task.add_done_callback(tasks.discard)

            if len(tasks) >= MAX_CONCURRENT_TASKS:
                _done, tasks = await asyncio.wait(tasks, return_when=asyncio.FIRST_COMPLETED)

            await asyncio.sleep(0.01)


if __name__ == '__main__':
    asyncio.run(load_test_api())
