import json
import pytest
from tests import client


def test_graphiql_get(client):
    response = client.get('/graphiql')
    assert response.status_code == 200


def test_home_get(client):
    response = client.get('/home')
    assert response.status_code == 200


def test_unknown_get(client):
    response = client.get('/not_real')
    assert response.status_code == 404


def test_graphiql_post(client):
    query = """query Hello { hello }"""
    response = client.post('/graphiql', json={'query': query})
    json_data = json.loads(response.data)
    hello = json_data["data"]["hello"]

    assert type(hello) is str


def test_api_post(client):
    query = """query Hello { hello }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    hello = json_data["data"]["hello"]

    assert type(hello) is str
