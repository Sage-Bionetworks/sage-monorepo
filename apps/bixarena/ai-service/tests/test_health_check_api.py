# coding: utf-8

from fastapi.testclient import TestClient


from bixarena_ai_service.models.basic_error import BasicError  # noqa: F401
from bixarena_ai_service.models.health_check import HealthCheck  # noqa: F401


def test_get_health_check(client: TestClient):
    """Test case for get_health_check

    Get health check information
    """

    headers = {
    }
    # uncomment below to make a request
    #response = client.request(
    #    "GET",
    #    "/health-check",
    #    headers=headers,
    #)

    # uncomment below to assert the status code of the HTTP response
    #assert response.status_code == 200

