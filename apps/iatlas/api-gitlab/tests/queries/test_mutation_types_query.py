import json
import pytest
from tests import NoneType


def test_mutation_types_query(client):
    query = """query MutationTypes {
        mutationTypes {
            display
            name
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    mutation_types = json_data['data']['mutationTypes']

    assert isinstance(mutation_types, list)
    for mutation_type in mutation_types:
        assert type(mutation_type['name']) is str
        assert type(mutation_type['display']) is str or NoneType
