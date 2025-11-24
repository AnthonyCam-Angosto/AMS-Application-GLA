from datetime import datetime, timedelta
import json
from unittest.mock import MagicMock, patch

import pandas as pd
from src.api.fetch import create_name, update_crypto_days, update_crypto_min, update_oldcrypto


def test_create_name():
    data=[[("Bitcoin",1),"bitcoin"],[("Binance Coin",2),"binancecoin"],[("xrp",3),"ripple"],
          [("usdcoin",4),"usd-coin"],[("avalanche",5),"avalanche-2"],
          [("shibainu",6),"shiba-inu"],[("cronos",7),"crypto-com-chain"],
          [("wrappedbitcoin",8),"wrapped-bitcoin"]]
    
    for temp in data:
        assert create_name(temp[0])==temp[1]

@patch("requests.get")
@patch("src.db.ecriture.add_all_oldcrypto_history")
def test_update_oldcrypto(mock_add,mock_get):

    fake_response = MagicMock()
    fake_response.status_code = 200
    fake_response.json.return_value = {
        "prices": [[1234567890, 100.0], [1234567999, 110.0]],
        "market_caps": [[1234567890, 2000000]],
        "total_volumes": [[1234567890, 50000]],
    }
    mock_get.return_value = fake_response

    date=datetime(2025,11,18)
    update_oldcrypto(("Bitcoin",1),date)

    mock_get.assert_called_once()
    args, kwargs = mock_get.call_args
    assert "bitcoin" in args[0]
    assert kwargs["params"]["to"] ==date.timestamp()

    mock_add.assert_called_once()
    call_args = mock_add.call_args[0]
    assert call_args[0] == 1
    ohlc_df = call_args[1]
    assert isinstance(ohlc_df, pd.DataFrame)

@patch("requests.get")
@patch("src.db.ecriture.add_all_oldcrypto_history")
def test_update_oldcrypto_error(mock_add,mock_get):

    fake_response = MagicMock()
    fake_response.status_code = 404
    fake_response.json.return_value = {"error":"limite range"}
    mock_get.return_value = fake_response

    date=datetime(2025,11,18)
    code=update_oldcrypto(("Bitcoin",1),date)
    assert code==-1

@patch("requests.get")
@patch("src.db.ecriture.add_all_crypto_history")
def test_update_crypto_days(mock_add,mock_get):
    fake_response = MagicMock()
    fake_response.status_code = 200
    fake_response.json.return_value = {
        "prices": [[1234567890, 100.0], [1234567999, 110.0]],
        "market_caps": [[1234567890, 2000000]],
        "total_volumes": [[1234567890, 50000]],
    }
    mock_get.return_value = fake_response

    date=datetime(2025,11,18)
    update_crypto_days(("Bitcoin",1))

    mock_get.assert_called_once()
    args, kwargs = mock_get.call_args
    assert "bitcoin" in args[0]
    assert kwargs["params"]["days"] ==7

    mock_add.assert_called_once()
    call_args = mock_add.call_args[0]
    assert call_args[0] == 1


@patch("requests.get")
@patch("src.db.ecriture.add_all_crypto_history")
def test_update_crypto_min(mock_add,mock_get):
    fake_response = MagicMock()
    fake_response.status_code = 200
    fake_response.json.return_value = [[1763404200000,79949.0,80187.0,79847.0,79847.0],[1763406000000,79732.0,80113.0,79732.0,79823.0],[1763407800000,79721.0,79744.0,79385.0,79385.0],[1763409600000,79453.0,79542.0,79092.0,79092.0],[1763411400000,78957.0,79295.0,78833.0,79295.0]]
    mock_get.return_value = fake_response

    date=datetime(2025,11,18)
    update_crypto_min(("Bitcoin",1))

    mock_add.assert_called_once()
    call_args = mock_add.call_args[0]
    assert call_args[0] == 1
