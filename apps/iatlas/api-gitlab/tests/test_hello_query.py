import json
import os
import pytest
from tests import client


def test_hello_query(client):
    query = """query Hello { hello }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    hello = json_data["data"]["hello"]

    assert type(hello) is str
