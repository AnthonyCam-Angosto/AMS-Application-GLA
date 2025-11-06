import psycopg2
from os import environ

password=environ.get("POSTGRES_PASSWORD")

_conn = psycopg2.connect(
    host="db",
    port=5432,
    user="docker",
    password=password,
    dbname="info_crypto"
)
_cur=_conn.cursor()




from .ecriture import *
from .lecture import *