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


@pytest.fixture(scope='module')
def common_query():
    return """query DriverResults($feature: [String!], $entrez: [Int!], $mutationCode: [String!], $tag: [String!], $dataSet: [String!]) {
        driverResults(feature: $feature, entrez: $entrez, mutationCode: $mutationCode, tag: $tag, dataSet: $dataSet) {
            dataSet { name }
            feature { name }
            gene { entrez }
            mutationCode { code }
            tag { name }
        }
    }"""


def test_driverResults_query_with_passed_data_set_entrez_feature_and_tag(client, common_query, data_set, feature_name, gene_entrez, tag_name):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
        'entrez': [gene_entrez],
        'feature': [feature_name],
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
        current_mutation_code = result['mutationCode']
        tag = result['tag']

        assert current_data_set['name'] == data_set
        assert feature['name'] == feature_name
        assert gene['entrez'] == gene_entrez
        if current_mutation_code:
            assert type(current_mutation_code['code']) is str
        assert tag['name'] == tag_name


def test_driverResults_query_with_passed_data_set_entrez_feature_and_mutation(client, common_query, data_set, feature_name, gene_entrez, mutation_code):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
        'entrez': [gene_entrez],
        'feature': [feature_name],
        'mutationCode': [mutation_code]
    }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        feature = result['feature']
        gene = result['gene']
        current_mutation_code = result['mutationCode']
        tag = result['tag']

        assert current_data_set['name'] == data_set
        assert feature['name'] == feature_name
        assert gene['entrez'] == gene_entrez
        assert current_mutation_code['code'] == mutation_code
        if tag:
            assert type(tag['name']) is str


def test_driverResults_query_with_passed_data_set_entrez_mutation_code_and_tag(client, common_query, data_set, gene_entrez, mutation_code, tag_name):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
        'entrez': [gene_entrez],
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
        current_mutation_code = result['mutationCode']
        tag = result['tag']

        assert current_data_set['name'] == data_set
        if feature:
            assert type(feature['name']) is str
        assert gene['entrez'] == gene_entrez
        assert current_mutation_code['code'] == mutation_code
        assert tag['name'] == tag_name


def test_driverResults_query_with_passed_data_set_feature_mutation_code_and_tag(client, common_query, data_set, feature_name, mutation_code, tag_name):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
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
        current_mutation_code = result['mutationCode']
        tag = result['tag']

        assert current_data_set['name'] == data_set
        assert feature['name'] == feature_name
        if gene:
            assert type(gene['entrez']) is int
        assert current_mutation_code['code'] == mutation_code
        assert tag['name'] == tag_name


def test_driverResults_query_with_passed_data_set_entrez_feature_mutation_code_and_tag(client, common_query, feature_name, gene_entrez, mutation_code, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
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
        current_mutation_code = result['mutationCode']
        tag = result['tag']

        assert type(current_data_set['name']) is str
        assert feature['name'] == feature_name
        assert gene['entrez'] == gene_entrez
        assert current_mutation_code['code'] == mutation_code
        assert tag['name'] == tag_name


def test_driverResults_query_with_no_arguments_no_relations(client):
    query = """query DriverResults($feature: [String!], $entrez: [Int!], $mutationCode: [String!], $tag: [String!], $dataSet: [String!]) {
        driverResults(feature: $feature, entrez: $entrez, mutationCode: $mutationCode, tag: $tag, dataSet: $dataSet, limit:10) {
            foldChange
            pValue
            log10PValue
            log10FoldChange
            numWildTypes
            numMutants
        }
    }"""
    response = client.post('/api', json={'query': query})
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
