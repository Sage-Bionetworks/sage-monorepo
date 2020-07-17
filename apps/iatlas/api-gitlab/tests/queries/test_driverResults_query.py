import json
import pytest
from tests import NoneType


@pytest.fixture(scope='module')
def feature_name():
    return 'Module11_Prolif_score'


@pytest.fixture(scope='module')
def gene_entrez():
    return 284058


@pytest.fixture(scope='module')
def mutation_code():
    return '(OM)'


@pytest.fixture(scope='module')
def tag_name():
    return 'BLCA'


def test_driverResults_query_with_passed_data_set_entrez_feature_mutation_code_and_tag(client, data_set, feature_name, gene_entrez, mutation_code, tag_name):
    query = """query DriverResults($feature: [String!], $entrez: [Int!], $mutationCode: [String!], $tag: [String!], $dataSet: [String!]) {
        driverResults(feature: $feature, entrez: $entrez, mutationCode: $mutationCode, tag: $tag, dataSet: $dataSet) {
            dataSet { name }
            feature { name }
            gene { entrez }
            mutationCode { code }
            tag { name }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'mutationCode': [mutation_code],
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        feature = result['feature']
        gene = result['gene']
        mutationCode = result['mutationCode']
        tag = result['tag']

        assert current_data_set['name'] == data_set
        assert feature['name'] == feature_name
        assert gene['entrez'] == gene_entrez
        assert mutationCode['code'] == mutation_code
        assert tag['name'] == tag_name


def test_driverResults_query_with_no_arguments_no_relations(client):
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


def test_driverResults_query_with_no_arguments_all_relations(client):
    query = """query DriverResults($feature: [String!], $entrez: [Int!], $mutationCode: [String!], $tag: [String!], $dataSet: [String!]) {
        driverResults(feature: $feature, entrez: $entrez, mutationCode: $mutationCode, tag: $tag, dataSet: $dataSet) {
            dataSet { name }
            feature { name }
            gene { entrez }
            mutationCode { code }
            tag { name }
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        feature = result['feature']
        gene = result['gene']
        mutation_code = result['mutationCode']
        tag = result['tag']

        assert type(current_data_set['name']) is str
        if feature:
            assert type(feature['name']) is str
        if gene:
            assert type(gene['entrez']) is int
        if mutation_code:
            assert type(mutation_code['code']) is str
        if tag:
            assert type(tag['name']) is str
