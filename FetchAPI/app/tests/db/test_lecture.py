from datetime import datetime
from unittest.mock import MagicMock, patch
from src.db import lecture

@patch("src.db.lecture.get_connection")
def test_get_cryptos(mock_connection):
    mock_cur=MagicMock()
    value=[("bitcoin",1),("etherum",2),("Tether",3),("Solana",4)]
    mock_cur.fetchall.return_value=value

    mock_conn=MagicMock()
    mock_conn.cursor.return_value=mock_cur

    mock_connection.return_value=mock_conn

    result=lecture.get_cryptos()
    assert result==value
    mock_cur.execute.assert_called_once_with("SELECT nom,crypto_id FROM cryptos")


@patch("src.db.lecture.get_connection")
def test_get_cryptos_null(mock_connection):
    mock_cur=MagicMock()
    value=[]
    mock_cur.fetchall.return_value=value
    mock_conn=MagicMock()
    mock_conn.cursor.return_value=mock_cur

    mock_connection.return_value=mock_conn

    result=lecture.get_cryptos()
    assert result==value
    mock_cur.execute.assert_called_once_with("SELECT nom,crypto_id FROM cryptos")


@patch("src.db.lecture.get_connection")
def test_dernier_date(mock_connection):
    mock_cur=MagicMock()
    value=(datetime(2025, 11, 18, 17, 40, 0),)
    mock_cur.fetchone.return_value=value
    mock_conn=MagicMock()
    mock_conn.cursor.return_value=mock_cur

    mock_connection.return_value=mock_conn

    result=lecture.dernier_date(1)
    assert result==value[0]
    mock_cur.execute.assert_called_once_with("SELECT max(date_time) from crypto_history where crypto_id=%s",(1,))