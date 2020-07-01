import json
import pytest
from tests import NoneType


def test_driverResults_query_with_relations(client):
    query = """query DriverResults($features: [Int!]!) {
        driverResults(features: $features) {
            mutationCode{
                code
            }
            foldChange
            pValue
            log10PValue
            log10FoldChange
            n_wt
            n_mut
            gene{
                hgnc
                entrez
                geneFamily
            }
            feature{
                name
                class
                display
            }
            tag{
                characteristics
                name
                color
            }
        }
    }"""
    features = [2]
    response = client.post(
        '/api', json={'query': query, 'variables': {'features': features}})
    json_data = json.loads(response.data)
    driverResults = json_data['data']['driverResults']
    assert isinstance(driverResults, list)
    for driverResult in driverResults[0:2]:
        gene = driverResult['gene']
        feature = driverResult['feature']
        tag = driverResult['tag']

        assert type(driverResult['foldChange']) is float or NoneType
        assert type(driverResult['pValue']) is float or NoneType
        assert type(driverResult['log10PValue']) is float or NoneType
        assert type(driverResult['log10FoldChange']) is float or NoneType
        assert type(driverResult['n_wt']) is int or NoneType
        assert type(driverResult['n_mut']) is int or NoneType

        if gene:
                assert type(gene['hgnc']) is str
                assert type(gene['entrez']) is int
                assert type(gene['geneFamily']) is str or NoneType
        if feature:
                assert type(feature['name']) is str
                assert type(feature['class']) is str or NoneType
                assert type(feature['display']) is str or NoneType
        if tag:
                assert type(tag['name']) is str
                assert type(tag['characteristics']) is str or NoneType
                assert type(tag['color']) is str or NoneType
   


def test_driverResults_query_no_relations(client):
    query = """query DriverResults($features: [Int!]!) {
        driverResults(features: $features) {
            foldChange
            pValue
            log10PValue
            log10FoldChange
            n_wt
            n_mut
        }
    }"""
    features = [2]
    response = client.post(
        '/api', json={'query': query, 'variables': {'features': features}})
    json_data = json.loads(response.data)
    driverResults = json_data['data']['driverResults']
    assert isinstance(driverResults, list)
    for driverResult in driverResults[0:2]:

        assert type(driverResult['foldChange']) is float or NoneType
        assert type(driverResult['pValue']) is float or NoneType
        assert type(driverResult['log10PValue']) is float or NoneType
        assert type(driverResult['log10FoldChange']) is float or NoneType
        assert type(driverResult['n_wt']) is int or NoneType
        assert type(driverResult['n_mut']) is int or NoneType
