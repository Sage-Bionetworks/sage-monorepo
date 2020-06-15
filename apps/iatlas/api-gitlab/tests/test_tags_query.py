import json
import pytest
from tests import client, NoneType


def test_tags_query(client):
    query = """query Tags {
        tags(dataSet: ["PCAWG"], related: ["Subtype"], feature: ["poof"]) {
            characteristics
            color
            display
            name
            sampleCount
            sampleIds
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["tags"]

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        assert type(data_set["characteristics"]) is str or NoneType
        assert type(data_set["color"]) is str or NoneType
        assert type(data_set["dislay"]) is str or NoneType
        assert type(data_set["name"]) is str
        assert type(data_set["sampleCount"]) is int
        assert isinstance(data_set["sampleIds"], list)
