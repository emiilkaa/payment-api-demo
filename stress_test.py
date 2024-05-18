import asyncio
import concurrent
import random
import string
from datetime import datetime, timedelta
from random import randrange

import requests

payments = {100: 100}


def random_date(start, end):
    delta = end - start
    int_delta = (delta.days * 24 * 60 * 60) + delta.seconds
    random_second = randrange(int_delta)
    return start + timedelta(seconds=random_second)


def execute_call():
    global payments
    try:
        urls = ['http://77.232.142.33:8080/api/v1/operations/pay', 'http://77.232.142.33:8080/api/v1/operations/cancel']

        d1 = datetime.strptime('4/18/2024 1:30 PM', '%m/%d/%Y %I:%M %p')
        d2 = datetime.strptime('5/17/2024 4:50 AM', '%m/%d/%Y %I:%M %p')
        operation_date = random_date(d1, d2).strftime('%Y-%m-%dT%H:%M:%S')
        N = random.randint(5, 9)
        url = random.choice(urls)
        if url == urls[0]:
            amount = random.uniform(100, 10000)
            request_body = {'amount': f'{amount:.2f}', 'terminalId': str(random.randint(10000, 99999)),
                            'operationDate': operation_date,
                            'cardInfo': {'cardNumber': str(random.randint(1234567812345678, 9999999945671234)),
                                         'expDate': str(random.randint(10, 12)) + str(random.randint(10, 28)),
                                         'cardHolderName': ''.join(random.choice(string.ascii_uppercase) for _ in range(N))},
                            'extension': {'country': 'Russia', 'currency': 'RUB', 'city': 'Moscow',
                                          'operationCode': str(random.randint(12412, 12491204))}, 'requestType': 'PAY'}
        else:
            original_request_id = random.choice(list(payments.keys()))
            amount = random.uniform(1, int(payments[original_request_id]))
            request_body = {'originalRequestId': original_request_id, 'amount': f'{amount:.2f}',
                            'terminalId': str(random.randint(10000, 99999)), 'operationDate': operation_date,
                            'extension': {'country': 'Russia', 'currency': 'RUB', 'city': 'Moscow',
                                          'operationCode': str(random.randint(12412, 12491204)), 'autoCancel': str(random.randint(0, 1))},
                            'requestType': 'CANCEL'}
        response = requests.request('POST', url, json=request_body, timeout=60)
        if (url == urls[0]) and (response.json()['status'] == 'SUCCESS'):
            payments[response.json()['requestId']] = amount
        if (url == urls[1]) and (response.json()['status'] == 'SUCCESS'):
            payments[original_request_id] -= amount
    except Exception as e:
        print(e)


async def load_test_api():
    loop = asyncio.get_event_loop()
    with concurrent.futures.ThreadPoolExecutor(max_workers=10) as pool:
        for i in range(100000):
            loop.run_in_executor(pool, execute_call)


if __name__ == '__main__':
    loop = asyncio.get_event_loop()
    loop.run_until_complete(load_test_api())
