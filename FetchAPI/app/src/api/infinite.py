from datetime import datetime, timedelta
from time import sleep
from src.api import fetch
from src.db.lecture import dernier_date

def old(cryptos:list[tuple]):
    current_date=datetime.now()
    for crypto in cryptos:
        print(crypto[0])
        date=dernier_date(crypto[1])

        if(date==None):
            fetch.update_oldcrypto(crypto,current_date-timedelta(weeks=1))
            fetch.update_crypto_days(crypto)

        elif(current_date.date()>date.date()):
            diff=current_date-date
            fetch.update_crypto_days(crypto,diff.days)

