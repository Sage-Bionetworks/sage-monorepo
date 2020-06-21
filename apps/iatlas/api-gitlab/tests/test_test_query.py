import json
import pytest
from tests import client


def test_test_query(client):
    query = """query Test { test }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    test = json_data['data']['test']

    assert type(test) is str
