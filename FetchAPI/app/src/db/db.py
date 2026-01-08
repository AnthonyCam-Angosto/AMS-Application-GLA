"""Gestion de la connexion à la base PostgreSQL.

Fournit une connexion singleton réutilisable via `get_connection()`.
"""

import os
import psycopg2

_conn = None


def get_connection():
    """Retourne une connexion active à la base de données.

    La connexion est créée une seule fois et stockée en mémoire pour
    réutilisation. Les paramètres de connexion utilisent la variable
    d'environnement `POSTGRES_PASSWORD` pour le mot de passe.

    Returns:
        psycopg2.extensions.connection: connexion PostgreSQL avec autocommit activé.
    """
    global _conn
    if _conn is None:
        password = os.getenv("POSTGRES_PASSWORD")
        _conn = psycopg2.connect(
            host="db",
            port=5432,
            user="docker",
            password=password,
            dbname="info_crypto",
        )
        _conn.autocommit = True
    return _conn