import datetime
import requests
import pandas as pd
from src.db import ecriture

api_key="CG-Z4QwuWyE7xytbJC9qcDQ2Pti"

def create_name(crypto):
    name_crypto=crypto[0].lower().replace(" ","")
    if(name_crypto=="xrp"):
        name_crypto="ripple"
    if(name_crypto=="usdcoin"):
        name_crypto="usd-coin"
    if(name_crypto=="avalanche"):
        name_crypto="avalanche-2"
    if(name_crypto=="shibainu"):
        name_crypto="shiba-inu"
    if(name_crypto=="cronos"):
        name_crypto="crypto-com-chain"
    if(name_crypto=="wrappedbitcoin"):
        name_crypto="wrapped-bitcoin"
    return name_crypto

def update_oldcrypto(crypto:tuple,date:datetime):
    start_date = date - datetime.timedelta(days=365-7)

    timestamp_from = int(start_date.timestamp())
    timestamp_to = int(date.timestamp())
    name_crypto=create_name(crypto)

    url = f"https://api.coingecko.com/api/v3/coins/{name_crypto}/market_chart/range"
    params={"vs_currency":"eur","from":timestamp_from,"to":timestamp_to,"x_cg_demo_api_key":api_key}

    response = requests.get(url,params=params)
    if response.status_code!=200:
        print(response,response.json())
        return -1
    data = response.json()

    prices = pd.DataFrame(data["prices"], columns=["timestamp", "price"])
    prices["date"] = pd.to_datetime(prices["timestamp"], unit="ms").dt.date

    ohlc = prices.groupby("date")["price"].agg(
        open="first",
        high="max",
        low="min",
        close="last"
    ).reset_index()

    ecriture.add_all_oldcrypto_history(crypto[1],ohlc)


def update_crypto_days(crypto:tuple,range=7):
    name_crypto=create_name(crypto)
    url = f"https://api.coingecko.com/api/v3/coins/{name_crypto}/ohlc"

    params = {"vs_currency": "eur", "days": range.__str__(),"x_cg_demo_api_key":api_key}
    response = requests.get(url,params=params)
    if response.status_code!=200:
        print(response,response.json())
    data = response.json()

    ecriture.add_all_crypto_history(crypto[1],data)


def update_crypto_min(crypto:tuple):
    name_crypto=create_name(crypto)
    url = f"https://api.coingecko.com/api/v3/coins/{name_crypto}/ohlc"

    params = {"vs_currency": "eur", "days": "1","x_cg_demo_api_key":api_key}
    response = requests.get(url,params=params)
    if response.status_code!=200:
        print(response,response.json())
    data = response.json()

    ecriture.add_all_crypto_history(crypto[1],[data[-1]])
