from datetime import datetime
from unittest.mock import MagicMock, patch

import pandas as pd
from src.db import ecriture

@patch("src.db.ecriture.get_connection")
def test_add_all_oldcrypto_history(mock_connection):
    data = {
        "date": "2025-11-18 10:00:00",
        "open": [100],
        "high": [120],
        "low": [90],
        "close": [115],
    }
    ohlc = pd.DataFrame(data)
    ohlc = pd.concat([ohlc]*5, ignore_index=True)

    mock_cur=MagicMock()
    mock_conn=MagicMock()
    mock_conn.cursor.return_value=mock_cur

    mock_connection.return_value=mock_conn

    ecriture.add_all_oldcrypto_history(1,ohlc)
    assert mock_cur.execute.call_count == len(ohlc)

@patch("src.db.ecriture.get_connection")
def test_add_all_crypto_history(mock_connection):
    data=[[datetime.timestamp(datetime.now()),100,50,90,200]]
    for i in range(5):
        data.append(data[0])

    mock_cur=MagicMock()
    mock_conn=MagicMock()
    mock_conn.cursor.return_value=mock_cur

    mock_connection.return_value=mock_conn

    ecriture.add_all_crypto_history(1,data)
    assert mock_cur.execute.call_count == len(data)