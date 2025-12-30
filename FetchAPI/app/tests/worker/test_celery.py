import pytest
from unittest.mock import patch, MagicMock
from celery import Celery
from src.worker import tasks
from src.worker import celery

def test_app_configuration():
    app = celery.app
    assert app.conf.broker_url == 'redis://localhost:6379/0'
    assert app.conf.result_backend == 'redis://localhost:6379/0'
    assert app.conf.timezone == 'Europe/Paris'
    assert 'update_crypto-toutes-5-minutes' in app.conf.beat_schedule


def test_worker_ready_signal_triggers_task(monkeypatch):
    fake_delay = MagicMock()
    monkeypatch.setattr(tasks.tache_demarrage, "delay", fake_delay)

    celery.on_worker_ready(sender=None)

    fake_delay.assert_called_once()
