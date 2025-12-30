import pytest
from unittest.mock import patch,MagicMock
from src.worker import tasks
from src.worker import celery


@pytest.fixture(autouse=True)
def celery_eager():
    celery.app.conf.task_always_eager = True
    celery.app.conf.task_eager_propagates = True




def test_tache_demarrage():
    fake_cryptos = ["BTC", "ETH"]

    with patch("src.worker.tasks.get_cryptos", return_value=fake_cryptos) as mock_get, \
         patch("src.worker.tasks.infinite.old") as mock_old:

        result = tasks.tache_demarrage.delay()

        mock_get.assert_called_once()
        mock_old.assert_called_once_with(fake_cryptos)
        assert result.get() is None


def test_task_main_success():
    fake_cryptos = ["BTC", "ETH"]

    with patch("src.worker.tasks.get_cryptos", return_value=fake_cryptos), \
         patch("src.worker.tasks.fetch.update_crypto_min") as mock_update, \
         patch("src.worker.tasks.task_success.inc") as mock_success, \
         patch("src.worker.tasks.task_failure.inc") as mock_failure, \
         patch("src.worker.tasks.task_duration.observe") as mock_duration:
        
        tasks.task_main.delay()

        assert mock_update.call_count == 2
        mock_success.assert_called_once()
        mock_failure.assert_not_called()
        mock_duration.assert_called_once()

