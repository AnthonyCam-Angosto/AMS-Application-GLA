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
    cryptos=get_cryptos()
    infinite.old(cryptos)
    print("Tâche de démarrage terminée.")


#TODO faire que la date soit celle de l'envoi et envoyer dans la fonction
@app.task(name="update_crypto")
def task_main():
    start = time.time()
    try:
        cryptos=get_cryptos()
        for crypto in cryptos:
            fetch.update_crypto_min(crypto)
            print(f"Mise à jour de {crypto} terminée à {time.strftime('%Y-%m-%d %H:%M:%S')}.")
        task_success.inc()
    except Exception:
        task_failure.inc()
        raise
    finally:
        task_duration.observe(time.time() - start)