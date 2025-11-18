from datetime import datetime
from pandas import DataFrame
from . import get_connection

def add_all_oldcrypto_history(crypto_id,ohlc:DataFrame):
    conn=get_connection()
    cur=conn.cursor()
    for _, row in ohlc.iterrows():
        cur.execute("""
            INSERT INTO crypto_history (crypto_id, date_time, open_price, high_price, low_price, close_price)
            VALUES (%s, %s, %s, %s, %s, %s)""", 
            (crypto_id, row["date"], row["open"], row["high"], row["low"], row["close"]))
    cur.close()


def add_all_crypto_history(crypto_id,historys:list[list]):
    conn=get_connection()
    cur=conn.cursor()
    for row in historys:
        dt = datetime.fromtimestamp(row[0] / 1000)
        cur.execute("""
            INSERT INTO crypto_history (crypto_id, date_time, open_price, high_price, low_price, close_price)
            VALUES (%s, %s, %s, %s, %s, %s)""", 
            (crypto_id, dt, row[1], row[2], row[3], row[4]))
    cur.close()
