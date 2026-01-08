"""Écriture des historiques de cryptomonnaies en base.

Ce module fournit des utilitaires pour insérer des séries temporelles
OHLC en base, soit depuis un DataFrame (historique ancien) soit depuis
le format renvoyé par l'API (timestamps en ms).
"""

from datetime import datetime
from pandas import DataFrame
from . import get_connection


def add_all_oldcrypto_history(crypto_id, ohlc: DataFrame):
    """Insère un DataFrame OHLC complet en base.

    Args:
        crypto_id: identifiant de la cryptomonnaie en base.
        ohlc (DataFrame): DataFrame contenant les colonnes `date`, `open`, `high`, `low`, `close`.
    """
    conn = get_connection()
    cur = conn.cursor()
    for _, row in ohlc.iterrows():
        cur.execute(
            """
            INSERT INTO crypto_history (crypto_id, date_time, open_price, high_price, low_price, close_price)
            VALUES (%s, %s, %s, %s, %s, %s)""",
            (crypto_id, row["date"], row["open"], row["high"], row["low"], row["close"]),
        )
    cur.close()


def add_all_crypto_history(crypto_id, historys: list[list]):
    """Insère une liste d'entrées OHLC fournie par l'API.

    Le format attendu est une liste de listes où chaque élément contient
    le timestamp en ms suivi des valeurs OHLC.

    Args:
        crypto_id: identifiant de la cryptomonnaie en base.
        historys (list[list]): liste d'enregistrements [timestamp_ms, open, high, low, close].
    """
    conn = get_connection()
    cur = conn.cursor()
    for row in historys:
        dt = datetime.fromtimestamp(row[0] / 1000)
        cur.execute(
            """
            INSERT INTO crypto_history (crypto_id, date_time, open_price, high_price, low_price, close_price)
            VALUES (%s, %s, %s, %s, %s, %s)""",
            (crypto_id, dt, row[1], row[2], row[3], row[4]),
        )
    cur.close()
