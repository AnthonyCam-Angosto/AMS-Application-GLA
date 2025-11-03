CREATE TABLE cryptos (
    crypto_id SERIAL PRIMARY KEY,
    symbole VARCHAR(5) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL
);

CREATE TABLE crypto_history (
    id BIGSERIAL PRIMARY KEY,
    crypto_id INT NOT NULL,     
    date_time TIMESTAMP NOT NULL,          
    open_price DECIMAL(18,8) NOT NULL,    
    high_price DECIMAL(18,8) NOT NULL,    
    low_price DECIMAL(18,8) NOT NULL, 
    close_price DECIMAL(18,8) NOT NULL, 
    volume DECIMAL(30,10),          
    FOREIGN KEY (crypto_id) REFERENCES cryptos(crypto_id)         
);

INSERT INTO cryptos (nom, symbole) VALUES
('Bitcoin', 'BTC'),
('Ethereum', 'ETH'),
('Binance Coin', 'BNB'),
('Tether', 'USDT'),
('Solana', 'SOL'),
('Cardano', 'ADA'),
('XRP', 'XRP'),
('Polkadot', 'DOT'),
('Dogecoin', 'DOGE'),
('USD Coin', 'USDC'),
('Avalanche', 'AVAX'),
('Shiba Inu', 'SHIB'),
('Litecoin', 'LTC'),
('Cronos', 'CRO'),
('Wrapped Bitcoin', 'WBTC');

