from locust import HttpUser, task, between
from urllib.parse import urlencode


class AgoraUser(HttpUser):
    wait_time = between(1, 3)  # Simulate realistic user wait times between requests

    @task
    def get_gene_comparison(self):
        """
        Test the gene comparison endpoint with the query parameters specified by the Gene Comparison
        web page.
        """
        query_params = {"pageSize": 10}
        encoded_params = urlencode(query_params)
        url = f"/v1/differentialExpression/rna?{encoded_params}"
        self.client.get(url)
