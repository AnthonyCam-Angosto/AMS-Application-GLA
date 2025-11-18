import os
import psycopg2

_conn=None

def get_connection():
    global _conn
    if _conn is None:
        password=os.getenv("POSTGRES_PASSWORD")
        _conn=psycopg2.connect(
            host="localhost",
            port=5432,
            user="docker",
            password=password,
            dbname="info_crypto"
        )
        _conn.autocommit=True
    return _conn