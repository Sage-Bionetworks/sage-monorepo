import json
import pytest
from tests import NoneType


@pytest.fixture(scope='module')
def feature_name():
    return 'AS'


@pytest.fixture(scope='module')
def mutation_code():
    return '(OM)'


@pytest.fixture(scope='module')
def tag_name():
    return 'BLCA'


def test_driverResults_query_with_passed_features(client, feature_name):
    query = """query DriverResults($feature: [String!], $entrez: [Int!], $mutationCode: [String!], $tag: [String!], $dataSet: [String!]) {
        driverResults(feature: $feature, entrez: $entrez, mutationCode: $mutationCode, tag: $tag, dataSet: $dataSet) {
            feature{
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'feature': [feature_name]}})
    json_data = json.loads(response.data)
    driver_results = json_data['data']['driverResults']
    assert isinstance(driver_results, list)
    assert len(driver_results) > 0
    for driver_result in driver_results[0:2]:
        feature = driver_result['feature']

        assert feature['name'] == feature_name


def test_driverResults_query_with_passed_entrez(client, entrez):
    query = """query DriverResults($feature: [String!], $entrez: [Int!], $mutationCode: [String!], $tag:[String!], $dataSet: [String!]) {
        driverResults(feature: $feature, entrez: $entrez, mutationCode: $mutationCode, tag:$tag,  dataSet: $dataSet) {
            gene{
                entrez
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [entrez]}})
    json_data = json.loads(response.data)
    driver_results = json_data['data']['driverResults']
    assert isinstance(driver_results, list)
    for driver_result in driver_results[0:2]:
        gene = driver_result['gene']
        assert gene['entrez'] == entrez


def test_driverResults_query_with_passed_mutationCode(client, mutation_code):
    query = """query DriverResults($feature: [String!], $entrez: [Int!], $mutationCode: [String!], $tag:[String!], $dataSet: [String!]) {
        driverResults(feature: $feature, entrez: $entrez, mutationCode: $mutationCode, tag:$tag,  dataSet: $dataSet) {
            mutationCode{
                code
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'mutationCode': mutation_code}})
    json_data = json.loads(response.data)
    driver_results = json_data['data']['driverResults']
    assert isinstance(driver_results, list)
    for driver_result in driver_results[0:2]:
        mutationCode = driver_result['mutationCode']
        assert mutationCode['code'] == mutation_code


def test_driverResults_query_with_passed_tags(client, tag_name):
    query = """query DriverResults($feature: [String!], $entrez: [Int!], $mutationCode: [String!], $tag:[String!], $dataSet: [String!]) {
        driverResults(feature: $feature, entrez: $entrez, mutationCode: $mutationCode, tag:$tag,  dataSet: $dataSet) {
            tag{
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'tag': [tag_name]}})
    json_data = json.loads(response.data)
    driver_results = json_data['data']['driverResults']
    assert isinstance(driver_results, list)
    for driver_result in driver_results[0:2]:
        tag = driver_result['tag']
        assert tag['name'] == tag_name


def test_driverResults_query_with_passed_data_sets(client, data_set):
    query = """query DriverResults($feature: [String!], $entrez: [Int!], $mutationCode: [String!], $tag:[String!], $dataSet: [String!]) {
        driverResults(feature: $feature, entrez: $entrez, mutationCode: $mutationCode, tag:$tag,  dataSet: $dataSet, limit: 10) {
            dataSet{
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [data_set]}})
    json_data = json.loads(response.data)
    driver_results = json_data['data']['driverResults']
    assert isinstance(driver_results, list)
    for driver_result in driver_results[0:2]:
        current_data_set = driver_result['dataSet']
        assert current_data_set['name'] == data_set


def test_driverResults_query_with_no_arguments(client):
    query = """query DriverResults($feature: [String!], $entrez: [Int!], $mutationCode: [String!], $tag: [String!], $dataSet: [String!]) {
        driverResults(feature: $feature, entrez: $entrez, mutationCode: $mutationCode, tag: $tag, dataSet: $dataSet, limit:10) {
            foldChange
            pValue
            log10PValue
            log10FoldChange
            n_wt
            n_mut
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {}})
    json_data = json.loads(response.data)
    driver_results = json_data['data']['driverResults']
    assert isinstance(driver_results, list)
    assert len(driver_results) > 0
    for driver_result in driver_results[0:2]:
        assert type(driver_result['foldChange']) is float or NoneType
        assert type(driver_result['pValue']) is float or NoneType
        assert type(driver_result['log10PValue']) is float or NoneType
        assert type(driver_result['log10FoldChange']) is float or NoneType
        assert type(driver_result['n_wt']) is int or NoneType
        assert type(driver_result['n_mut']) is int or NoneType
