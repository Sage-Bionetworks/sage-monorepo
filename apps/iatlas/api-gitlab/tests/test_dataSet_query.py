import json
import pytest
from tests import client, NoneType


def test_dataSet_query(client):
    query = """query DataSet {
        dataSet(name: ["PCAWG"], group: ["Subtype"], feature: ["poof"]) {
            sampleGroup
            groupName
            groupSize
            characteristics
            color
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["dataSet"]

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        assert type(data_set["sampleGroup"]) is str
        assert type(data_set["groupName"]) is str or NoneType
        assert type(data_set["groupSize"]) is int
        assert type(data_set["characteristics"]) is str or NoneType
        assert type(data_set["color"]) is str or NoneType
