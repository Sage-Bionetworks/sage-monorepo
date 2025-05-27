from locust import HttpUser, task, between
from urllib.parse import urlencode
import random
import string


class AgoraUser(HttpUser):
    wait_time = between(1, 3)  # Simulate realistic user wait times between requests

    # @task
    # def get_differential_expression_random_page_number(self):
    #     """
    #     Test the gene comparison endpoint with a random pageNumber between 0 and 2000.
    #     """
    #     # TODO: max page number must be fetched programmatically.
    #     page_number = random.randint(0, 2000)
    #     query_params = {"pageSize": 10, "pageNumber": page_number}
    #     encoded_params = urlencode(query_params)
    #     url = f"/v1/differentialExpression/rna?{encoded_params}"
    #     self.client.get(url)

    @task
    def get_differential_expression_random_search_term(self):
        """
        Test the gene comparison endpoint with the query parameters specified by the Gene Comparison
        web page.
        """
        # Generate a random two-letter string
        search_term = "".join(random.choices(string.ascii_lowercase, k=2))
        query_params = {"pageSize": 10, "searchTerm": search_term}
        encoded_params = urlencode(query_params)
        url = f"/v1/differentialExpression/rna?{encoded_params}"
        self.client.get(url)

    # @task
    # def get_differential_expression_gene_comparison_page(self):
    #     """
    #     Test the gene comparison web page.
    #     """
    #     url = f"/genes/comparison"
    #     self.client.get(url)
