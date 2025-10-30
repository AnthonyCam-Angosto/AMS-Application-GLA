from prometheus_client import start_http_server, Counter, Gauge, Histogram

# Définir des métriques
REQUEST_COUNT = Counter('app_requests_total', 'Nombre total de requêtes')
LATENCY = Histogram('app_request_latency_seconds', 'Latence des requêtes en secondes')

def process_request():
    REQUEST_COUNT.inc()
    # a modifier exemple


def start_prometheus_server(port=8000):
    start_http_server(port)
    print(f"Métriques disponibles sur http://localhost:{port}/metrics")

if __name__ == "__main__":
    start_prometheus_server()
    # Simuler des requêtes
    import time
    while True:
        process_request()
        time.sleep(1)