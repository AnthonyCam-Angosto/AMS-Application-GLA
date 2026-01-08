import os
import random
import string
from locust import HttpUser, task, between


def rand_string(n=8):
    return ''.join(random.choices(string.ascii_lowercase + string.digits, k=n))


class CryptoViewUser(HttpUser):
    wait_time = between(1, 3)

    def on_start(self):
        # credentials via env vars pour tests authentifiés
        self.username = os.getenv('LOCUST_USER', 'admin')
        self.password = os.getenv('LOCUST_PASS', 'admin')
        self.authenticated = False

        # tentative d'authentification simple (sans CSRF)
        try:
            r = self.client.post('/login', {'username': self.username, 'password': self.password}, allow_redirects=True)
            if r.status_code in (200, 302):
                self.authenticated = True
        except Exception:
            self.authenticated = False

    @task(3)
    def browse_index_and_assets(self):
        self.client.get('/', name='GET /')
        self.client.get('/styles/main.css', name='GET /styles/main.css')

    @task(4)
    def login_flow(self):
        with self.client.post('/login', {'username': self.username, 'password': self.password}, name='POST /login', allow_redirects=True, catch_response=True) as res:
            if res.status_code not in (200, 302):
                res.failure(f'login failed: {res.status_code}')
            else:
                res.success()
                self.authenticated = True

    @task(2)
    def register_user(self):
        username = f'user_{rand_string(6)}'
        email = f'{username}@example.com'
        password = 'Test1234'
        with self.client.post('/inscription', {'identifiant': username, 'email': email, 'password': password}, name='POST /inscription', allow_redirects=True, catch_response=True) as resp:
            if resp.status_code >= 400:
                resp.failure(f"inscription failed: {resp.status_code}")
            else:
                resp.success()

    @task(6)
    def dashboard_and_ohlc(self):
        self.client.get('/dashboard', name='GET /dashboard')
        params = {'limit': random.choice(['50', '100']), 'symbol': random.choice(['bitcoin', 'ethereum', 'ripple'])}
        with self.client.get('/dashboard/ohlc', params=params, name='GET /dashboard/ohlc', catch_response=True) as r:
            if r.status_code != 200:
                r.failure('ohlc failed')

    @task(3)
    def profile_workflow(self):
        # accéder à la page profile
        with self.client.get('/profile', name='GET /profile', catch_response=True) as r:
            if r.status_code != 200:
                r.failure('profile page failed')
            else:
                r.success()

        # changer email / mot de passe (sans CSRF)
        new_email = f'test_{rand_string(5)}@example.com'
        self.client.post('/profile/change-email', {'newEmail': new_email}, name='POST /profile/change-email', allow_redirects=True)
        self.client.post('/profile/change-password', {'oldPassword': 'oldpass', 'newPassword': 'NewPass123'}, name='POST /profile/change-password', allow_redirects=True)

    @task(1)
    def logout(self):
        self.client.get('/logout', name='GET /logout', allow_redirects=True)