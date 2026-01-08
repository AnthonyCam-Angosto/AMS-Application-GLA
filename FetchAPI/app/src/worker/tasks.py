"""Tâches Celery liées à la mise à jour des données de cryptomonnaies.

Contient les tâches déclenchées par le scheduler et les compteurs
Prometheus pour le monitoring des exécutions.
"""

from .celery import app
from prometheus_client import Counter, Histogram
import time
from src.db.lecture import get_cryptos
from src.api import infinite
from src.api import fetch

task_success = Counter("db_update_success_total", "Nombre de mises à jour BD réussies")
task_failure = Counter("db_update_failure_total", "Nombre de mises à jour BD échouées")
task_duration = Histogram("db_update_duration_seconds", "Durée des mises à jour BD")


@app.task(name="demarrage")
def tache_demarrage(*args, **kwargs):
    """Tâche exécutée au démarrage du worker.

    Récupère la liste des cryptomonnaies et lance la récupération
    des historiques manquants via `infinite.old`.
    """
    cryptos = get_cryptos()
    infinite.old(cryptos)
    print("Tâche de démarrage terminée.")


@app.task(name="update_crypto")
def task_main():
    """Tâche principale périodique qui met à jour les données.

    Pour chaque crypto, récupère les derniers OHLC et les insère en base.
    Les métriques Prometheus sont mises à jour pour le succès, l'échec
    et la durée d'exécution.
    """
    start = time.time()
    try:
        cryptos = get_cryptos()
        for crypto in cryptos:
            fetch.update_crypto_min(crypto)
            print(
                f"Mise à jour de {crypto} terminée à {time.strftime('%Y-%m-%d %H:%M:%S')}."
            )
        task_success.inc()
    except Exception:
        task_failure.inc()
        raise
    finally:
        task_duration.observe(time.time() - start)