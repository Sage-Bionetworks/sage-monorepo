from locust import HttpUser, task, between


class AgoraUser(HttpUser):
    wait_time = between(1, 5)

    @task
    def hello_world(self):
        self.client.get("/")
        self.client.get("/about")
