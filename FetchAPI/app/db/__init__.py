import psycopg2

_conn = psycopg2.connect(
    host="db",
    port=5432,
    user="docker",
    password="docker",
    dbname="info_crypto"
)
_cur=_conn.cursor()




from .ecriture import *
from .lecture import *