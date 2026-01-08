# cryptoview

## Documentation technique et utilisateur

### Vue d'ensemble
`cryptoview` est une plateforme de collecte, de stockage et d'analyse de données de marchés (cryptomonnaies). Le projet contient deux composants principaux :
- Une application de collecte et de gestion des données (FetchAPI).
- Une application web d'analyse et de visualisation (ServeurWeb).

Le service de collecte utilise l'API publique CoinGecko comme source principale des données de marché.

### 1) Application de collecte (technique)
- Fonctionnement : service s'exécutant en continu et collectant périodiquement des données depuis l'API CoinGecko.
- Données collectées : prix en temps réel, volume d'échange, capitalisation, variations (horaire/journalière), indicateurs techniques, etc.
- Stockage : enregistrement des données dans une base (par défaut PostgreSQL, mais InfluxDB ou MongoDB peuvent être utilisés selon le besoin).
- Tolérance et ordonnancement : un scheduler automatise la fréquence de collecte. La répartition et la tolérance aux pannes sont assurées via une file/gestionnaire de tâches (ex. Celery + Redis). Les tâches de collecte peuvent être replanifiées ou reprises en cas d'échec.

### 2) Application web d'analyse et visualisation (utilisateur)
Fonctionnalités principales :
- Visualisations dynamiques : graphiques en courbes, chandeliers, heatmaps, indicateurs techniques personnalisables.
- Filtres temporels et multi-actifs : sélection de plages temporelles, comparaison de plusieurs cryptomonnaies.
- Alertes personnalisées : configuration de seuils (prix, variation) et règles d'alerte.
- Notifications : envoi d'e-mails ou de webhooks (ex. Discord) pour les alertes et notifications.
- Module de prévision : modèles simples (moyennes mobiles, régression linéaire) pour fournir des indications de tendance.
- Portefeuille virtuel : simuler des ordres d'achat/vente pour évaluer des scénarios et performances hypothétiques.
- Authentification et rôles : système sécurisé (JWT/OAuth2) et gestion des rôles (administrateur, utilisateur).

### Architecture & déploiement
Le dépôt contient une configuration Docker Compose pour orchestrer les services : workers de collecte (`fetchapi-worker`), scheduler (`fetchapi-beat`), Redis, base de données (`db`) et l'application web (`ServeurWeb`). Voir le fichier `compose.yaml` pour la configuration complète.

Emplacements principaux :
- Code de collecte : dossier `FetchAPI`.
- Application web : dossier `ServeurWeb`.

### Démarrage rapide (local avec Docker)
1. Depuis la racine du dépôt, construire et lancer les services :

# cryptoview

## Documentation Technique

### Vue d'ensemble
`cryptoview` est une plateforme de collecte, stockage et d'analyse de données de marché pour les cryptomonnaies. Le dépôt contient deux composants principaux : la collecte de données (`FetchAPI`) et l'application web d'analyse (`ServeurWeb`).

### Architecture
- Services principaux : collecte (FetchAPI), worker/scheduler (Celery/Redis), base de données (PostgreSQL), application web (ServeurWeb).
- Orchestration : configuration Docker Compose fournie (`compose.yaml`) ; manifeste Kubernetes minimal (`deployment.yaml`) pour déploiements locaux (Minikube).

### Détails techniques
- Source de données : CoinGecko (par défaut). Peut être remplacée via configuration.
- Stockage : PostgreSQL (par défaut). Schémas et scripts SQL se trouvent dans `db/tables`.
- Tâches asynchrones : Celery + Redis (FetchAPI) pour la répétition et la tolérance des collectes.
- Tests : tests unitaires et de performance disponibles dans `FetchAPI/tests` et `ServeurWeb/src/test` / `ServeurWeb/test_perf`.

### Installation et démarrage (local avec Docker)
1. Construire et lancer les services depuis la racine :

```powershell
docker compose up --build
```

2. Accéder à l'application web : http://localhost:8080

### Déploiement avec Minikube (rapide)
```powershell
minikube start
minikube image build -t ams-application-gla-db:latest ./db
minikube image build -t ams-application-gla-fetchapi-worker:latest ./FetchAPI
minikube image build -t ams-application-gla-serveurweb:latest ./ServeurWeb
kubectl apply -f deployment.yaml
kubectl port-forward deployment/serveurweb 8080:80
```

### Configuration et variables d'environnement utiles
- `POSTGRES_PASSWORD` : mot de passe PostgreSQL.
- Variables pour l'API source et fréquence de collecte se trouvent dans `FetchAPI` (vérifier `requirements.txt` et fichiers de configuration).

### Tests de charge (Locust)
Exemple (PowerShell) :
```powershell
#$env:LOCUST_USER = 'admin'
#$env:LOCUST_PASS = 'admin'
locust -f ServeurWeb/test_perf/locustfile.py --headless -u 100 -r 10 --run-time 5m --host http://localhost:8080 --csv=locust_report
```

## Documentation Utilisateur

### Objectif
Permettre aux utilisateurs de consulter des historiques de prix, visualiser des indicateurs techniques, configurer des alertes et tester des scénarios via un portefeuille virtuel.

### Fonctionnalités principales
- Visualisations : graphiques (courbe, chandelier), comparaisons multi-actifs.
- Filtres temporels : sélection de plage (1H, 24H, 7D, 1M, 1Y, etc.).
- Alertes : création d'alertes basées sur le prix ou la variation (%), envoi via e-mail ou webhook.
- Portefeuille virtuel : simuler ordres d'achat/vente et suivre la performance.
- Authentification : accès sécurisé (compte utilisateur, rôles).

### Utilisation rapide
1. Ouvrir l'application : http://localhost:8080
2. Se connecter / créer un compte.
3. Rechercher une cryptomonnaie via la barre de recherche.
4. Visualiser le graphique et sélectionner la plage temporelle.
5. Pour créer une alerte : menu `Alertes` → `Nouvelle alerte` → définir conditions et notification.
6. Pour tester un scénario dans le portefeuille virtuel : `Portefeuille` → `Nouvel ordre` → simuler achat/vente.

### API (accès rapide)
- Endpoint principal (exemples) :
	- `GET /api/cryptos` : liste des cryptomonnaies disponibles.
	- `GET /api/cryptos/{symbole}/price` : prix courant (dernière valeur).
	- `POST /api/alerts` : créer une alerte.

Consulter les routes dans le code source `ServeurWeb/src/main` pour la liste complète et les schémas JSON.

### Dépannage courant
- Pas de données : vérifier que les workers FetchAPI sont démarrés et que la base PostgreSQL est accessible.
- Erreurs d'authentification : vérifier les variables d'environnement et la configuration du provider d'auth (JWT/SMTP).

---

Si vous souhaitez, je peux :
- Ajouter des exemples d'appels API détaillés (cURL) pour chaque endpoint.
- Documenter le schéma de la base de données dans un fichier séparé.
