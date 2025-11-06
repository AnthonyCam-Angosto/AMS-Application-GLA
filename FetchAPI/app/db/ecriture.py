from . import _cur

def add_crypto_history(crypto,date,open,high,low,close):
    insert_query="""INSERT INTO crypto_history (
        crypto_id, date_time, open_price, high_price, low_price, close_price
    ) VALUES (%s, %s, %s, %s, %s, %s)"""
    _cur.execute(insert_query,crypto,date,open,high,low,close)
