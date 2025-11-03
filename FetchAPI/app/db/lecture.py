from . import _cur

def get_cryptos():
    _cur.execute("SELECT nom,id FROM cryptos")
    return _cur.fetchall()

def dernier_date(crypto):
    # TODO: a faire
    pass


if __name__ == "__main__":
    get_cryptos()