"""Configuration Celery pour l'application FetchAPI.

Ce module crée et configure l'instance Celery, définit le planning
de tâches périodiques et déclenche la tâche de démarrage lorsque
le worker est prêt.
"""

from celery import Celery
from celery.schedules import crontab
from celery.signals import worker_ready

app = Celery(
    'FetchAPI',
    broker='redis://redis:6379/0',
    backend='redis://redis:6379/0'
)

app.conf.timezone = 'Europe/Paris'

from src.worker import tasks

app.conf.beat_schedule = {
    'update_crypto-toutes-5-minutes': {
        'task': 'update_crypto',
        'schedule': crontab(minute='*/5',),
    },
}


@worker_ready.connect
def on_worker_ready(sender, **kwargs):
    """Handler appelé quand le worker Celery est prêt.

    Lance la tâche d'initialisation `tache_demarrage` en arrière-plan.
    """
    tasks.tache_demarrage.delay()