from locust import HttpUser, task, between

class SpringUser(HttpUser):
    wait_time = between(1, 3)  # pause entre deux requêtes

    @task
    def get_ping(self):
        self.client.get("/ping")
