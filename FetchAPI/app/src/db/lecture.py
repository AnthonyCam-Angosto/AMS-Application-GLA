from . import get_connection

def get_cryptos()->list[tuple]|None:
    conn=get_connection()
    cur=conn.cursor()
    cur.execute("SELECT nom,crypto_id FROM cryptos")
    result=cur.fetchall()
    cur.close()
    return result

def dernier_date(crypto_id:int):
    conn=get_connection()
    cur=conn.cursor()
    cur.execute("SELECT max(date_time) from crypto_history where crypto_id=%s",(crypto_id,))
    result=cur.fetchone()
    cur.close()
    return result[0]

