from datetime import datetime
from src.db.lecture import get_cryptos
from src.api import infinite


def start():
    cryptos=get_cryptos()
    infinite.old(cryptos)
    infinite.main_loop(cryptos)