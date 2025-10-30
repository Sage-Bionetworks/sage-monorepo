from locust import HttpUser, task, between
from urllib.parse import urlencode


class AgoraUser(HttpUser):
    wait_time = between(1, 3)  # Simulate realistic user wait times between requests

    @task
    def get_home_page(self):
        """
        Test the Agora home page load time and availability.
        """
        self.client.get("/")

    @task
    def get_gene_comparison(self):
        """
        Test the gene comparison endpoint with the query parameters specified by the Gene Comparison
        web page.
        """
        query_params = {
            "category": "RNA - Differential Expression",
            "subCategory": "AD Diagnosis (males and females)",
        }
        encoded_params = urlencode(query_params)
        url = f"/api/v1/genes/comparison?{encoded_params}"
        self.client.get(url)

    @task
    def get_nominated_targets(self):
        """
        Test the nominated targets gene endpoint used by the Nominated Targets web page.
        """
        self.client.get("/api/v1/genes/nominated")
