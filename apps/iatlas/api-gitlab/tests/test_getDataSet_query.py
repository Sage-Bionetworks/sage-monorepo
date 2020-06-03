import json
import pytest
from tests import client


def test_getDataSet_query(client):
    query = """query DataSet {
        getDataSet(name: ["PCAWG"], group: ["Subtype"], feature: ["poof"]) {
            sampleGroup
            groupName
            groupSize
            characteristics
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    data_set = json_data["data"]["getDataSet"]

    assert data_set["sampleGroup"] == "PCAWG"
    assert data_set["groupName"] == "Subtype"
    assert data_set["groupSize"] == 42
    assert data_set["characteristics"] == "poof"
