from celery import Celery
from celery.schedules import crontab
from celery.signals import worker_ready

app = Celery(
    'FetchAPI',
    broker='redis://localhost:6379/0',
    backend='redis://localhost:6379/0'
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
    tasks.tache_demarrage.delay()