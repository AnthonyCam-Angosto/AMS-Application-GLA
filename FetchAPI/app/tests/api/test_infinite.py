from datetime import datetime
from unittest.mock import patch

from src.api import infinite

@patch("src.api.fetch.update_oldcrypto")
@patch("src.api.fetch.update_crypto_days")
@patch("src.api.infinite.dernier_date")
def test_old(mock_dernier_date,mock_update_days,mock_update_old):
    data=[("bitcoin",1),("etherum",2),("Dogecoin",3)]
    mock_dernier_date.return_value=None

    infinite.old(data)
    assert mock_update_old.call_count == len(data)
    assert mock_update_days.call_count == len(data)


@patch("src.api.fetch.update_oldcrypto")
@patch("src.api.fetch.update_crypto_days")
@patch("src.api.infinite.dernier_date")
def test_old2(mock_dernier_date,mock_update_days,mock_update_old):
    data=[("bitcoin",1),("etherum",2),("Dogecoin",3)]
    mock_dernier_date.return_value=datetime(2024, 11, 18, 12, 0)

    infinite.old(data)
    assert mock_update_old.call_count == 0
    assert mock_update_days.call_count == len(data)

