"""Accès en lecture aux données de cryptomonnaies en base.

Ce module expose des fonctions utilitaires pour récupérer la liste
des cryptomonnaies et la dernière date d'enregistrement pour une
crypto donnée.
"""

from . import get_connection


def get_cryptos() -> list[tuple] | None:
    """Récupère la liste des cryptomonnaies enregistrées.

    Returns:
        list[tuple] | None: Liste de tuples `(nom, crypto_id)` ou `None` si aucune.
    """
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT nom,crypto_id FROM cryptos")
    result = cur.fetchall()
    cur.close()
    return result


def dernier_date(crypto_id: int):
    """Retourne la dernière date d'enregistrement pour une crypto.

    Args:
        crypto_id (int): identifiant de la cryptomonnaie en base.

    Returns:
        datetime.datetime | None: la date la plus récente ou None si aucune donnée.
    """
    conn = get_connection()
    cur = conn.cursor()
    cur.execute(
        "SELECT max(date_time) from crypto_history where crypto_id=%s", (crypto_id,)
    )
    result = cur.fetchone()
    cur.close()
    return result[0]

