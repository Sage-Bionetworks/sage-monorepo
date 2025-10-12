from fastapi.testclient import TestClient


def test_health_check_returns_pass(client: TestClient):
    response = client.get("/health-check")
    assert response.status_code == 200
    data = response.json()
    assert data == {"status": "pass"}
