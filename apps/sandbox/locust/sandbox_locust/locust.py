from locust import HttpUser, task


class OpenChallengesUser(HttpUser):
    @task
    def hello_world(self):
        self.client.get("/")
        self.client.get("/about")
