import json
import pytest
from tests import client


def test_dataSet_query(client):
    query = """query DataSet {
        dataSet(name: ["PCAWG"], group: ["Subtype"], feature: ["poof"]) {
            sampleGroup
            groupName
            groupSize
            characteristics
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["dataSet"]

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        assert data_set["sampleGroup"] == "PCAWG"
        assert data_set["groupName"] == "Subtype"
        assert data_set["groupSize"] == 42
        assert data_set["characteristics"] == "poof"
