from datetime import datetime, timedelta
from time import sleep
from src.api import fetch
from src.db.lecture import *

def old(cryptos:list[tuple]):
    current_date=datetime.now()
    for crypto in cryptos:
        print(crypto[0])
        date=dernier_date(crypto[1])

        if(date==None):
            fetch.update_oldcrypto(crypto,current_date-timedelta(weeks=1))
            fetch.update_crypto_days(crypto,current_date)

        elif(current_date.date()>date.date()):
            diff=current_date-date
            fetch.update_crypto_days(crypto,current_date,diff.days)
        sleep(1.5)


def main_loop(cryptos:list[tuple]):
    time_sleep=60*6
    print("start boucle")
    while True:
        sleep(time_sleep)
        current_date=datetime.now()
        for crypto in cryptos:
            fetch.update_crypto_min(crypto,current_date)