"""Helpers pour appeler l'API CoinGecko et préparer les données.

Ce module contient des fonctions utilitaires pour normaliser les noms
de cryptomonnaies, récupérer des historiques et transformer les
réponses en formats insérables en base.
"""

import datetime
import requests
import pandas as pd
from src.db import ecriture

api_key = "CG-Z4QwuWyE7xytbJC9qcDQ2Pti"


def create_name(crypto):
    """Normalise un nom de cryptomonnaie pour les endpoints CoinGecko.

    Args:
        crypto (tuple): tuple dont le premier élément est le nom lisible.

    Returns:
        str: nom normalisé attendu par CoinGecko.
    """
    name_crypto = crypto[0].lower().replace(" ", "")
    if name_crypto == "xrp":
        name_crypto = "ripple"
    if name_crypto == "usdcoin":
        name_crypto = "usd-coin"
    if name_crypto == "avalanche":
        name_crypto = "avalanche-2"
    if name_crypto == "shibainu":
        name_crypto = "shiba-inu"
    if name_crypto == "cronos":
        name_crypto = "crypto-com-chain"
    if name_crypto == "wrappedbitcoin":
        name_crypto = "wrapped-bitcoin"
    return name_crypto


def update_oldcrypto(crypto: tuple, date: datetime.datetime):
    """Récupère et insère un historique annuel (rolling) pour une crypto.

    Args:
        crypto (tuple): tuple `(nom, crypto_id)`.
        date (datetime.datetime): date de fin pour l'historique.
    """
    start_date = date - datetime.timedelta(days=365 - 7)

    timestamp_from = int(start_date.timestamp())
    timestamp_to = int(date.timestamp())
    name_crypto = create_name(crypto)

    url = f"https://api.coingecko.com/api/v3/coins/{name_crypto}/market_chart/range"
    params = {
        "vs_currency": "eur",
        "from": timestamp_from,
        "to": timestamp_to,
        "x_cg_demo_api_key": api_key,
    }

    response = requests.get(url, params=params)
    if response.status_code != 200:
        print(response, response.json())
        return -1
    data = response.json()

    prices = pd.DataFrame(data["prices"], columns=["timestamp", "price"])
    prices["date"] = pd.to_datetime(prices["timestamp"], unit="ms").dt.date

    ohlc = (
        prices.groupby("date")["price"]
        .agg(open="first", high="max", low="min", close="last")
        .reset_index()
    )

    ecriture.add_all_oldcrypto_history(crypto[1], ohlc)


def arrondir_jours(valeur: int) -> int:
    """Convertit un nombre de jours en la valeur acceptée par l'API.

    L'API n'accepte que certaines valeurs; on retourne la valeur minimale
    valide supérieure ou égale à la valeur demandée.
    """
    # valeurs acceptées par l'API
    valeurs_valides = [1, 7, 14, 30, 90, 180, 365]

    if valeur >= max(valeurs_valides):
        return max(valeurs_valides)

    for v in valeurs_valides:
        if v >= valeur:
            return v
    return max(valeurs_valides)


def update_crypto_days(crypto: tuple, _range=7):
    """Récupère et insère l'historique OHLC pour une plage de jours donnée.

    Args:
        crypto (tuple): tuple `(nom, crypto_id)`.
        _range (int): nombre de jours demandé (sera arrondi).
    """
    name_crypto = create_name(crypto)
    url = f"https://api.coingecko.com/api/v3/coins/{name_crypto}/ohlc"
    _range = arrondir_jours(_range)

    params = {"vs_currency": "eur", "days": _range, "x_cg_demo_api_key": api_key}
    response = requests.get(url, params=params)
    if response.status_code != 200:
        print(response, response.json())
    data = response.json()

    ecriture.add_all_crypto_history(crypto[1], data)


def update_crypto_min(crypto: tuple):
    """Récupère l'OHLC le plus récent (1 jour) et l'insère en base.

    Args:
        crypto (tuple): tuple `(nom, crypto_id)`.
    """
    name_crypto = create_name(crypto)
    url = f"https://api.coingecko.com/api/v3/coins/{name_crypto}/ohlc"

    params = {"vs_currency": "eur", "days": "1", "x_cg_demo_api_key": api_key}
    response = requests.get(url, params=params)
    if response.status_code != 200:
        print(response, response.json())
    data = response.json()

    ecriture.add_all_crypto_history(crypto[1], [data[-1]])
