import json
import pytest


def test_graphiql_get(client):
    response = client.get('/graphiql')
    assert response.status_code == 200


def test_healthcheck_get(client):
    response = client.get('/healthcheck')
    assert response.status_code == 200


def test_unknown_get(client):
    response = client.get('/not_real')
    assert response.status_code == 404


def test_graphiql_post(client):
    query = """query Test { test { items { userAgent } } }"""
    response = client.post('/graphiql', json={'query': query})
    json_data = json.loads(response.data)
    user_agent = json_data['data']['test']['items']['userAgent']

    assert type(user_agent) is str


def test_api_post(client):
    query = """query Test { test { items { userAgent } } }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    user_agent = json_data['data']['test']['items']['userAgent']

    assert type(user_agent) is str
