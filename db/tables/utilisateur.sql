CREATE TYPE role_user AS ENUM ('ADMIN', 'UTILISATEUR');
CREATE TYPE type_transac AS ENUM ('ENTREE', 'SORTIE');

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    identifiant VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role role_user NOT NULL DEFAULT 'utilisateur',
    UNIQUE(identifiant,email)
);

CREATE TABLE portefeuilles(
    id_portefeuille SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    nom VARCHAR(100) NOT NULL,
    date_creation TIMESTAMP, 
    UNIQUE(nom),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE comptes(
    id_compte SERIAL PRIMARY KEY,
    id_portefeuille INT NOT NULL,
    crypto_id INT NOT NULL,
    solde DECIMAL(20,10) NOT NULL,
    FOREIGN KEY (id_portefeuille) REFERENCES portefeuilles(id_portefeuille),
    FOREIGN KEY (crypto_id) REFERENCES cryptos(crypto_id)     
);

CREATE TABLE transactions(
    id_transaction BIGSERIAL PRIMARY KEY,
    id_compte INT NOT NULL,
    type type_transac NOT NULL,
    montant DECIMAL(20,10) NOT NULL,
    date_transaction TIMESTAMP NOT NULL,
    FOREIGN KEY (id_compte) REFERENCES comptes(id_compte) 
);