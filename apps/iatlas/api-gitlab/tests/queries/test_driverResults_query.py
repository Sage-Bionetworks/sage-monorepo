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
    return """query DriverResults(
        $dataSet: [String!]
        $entrez: [Int!]
        $feature: [String!]
        $mutationCode: [String!]
        $tag: [String!]
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minFoldChange: Float
        $minLog10FoldChange: Float
        $minNumWildTypes: Int
        $minNumMutants: Int
    ) {
        driverResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            mutationCode: $mutationCode
            tag: $tag
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minFoldChange: $minFoldChange
            minLog10FoldChange: $minLog10FoldChange
            minNumWildTypes: $minNumWildTypes
            minNumMutants: $minNumMutants
        ) {
            pValue
            log10PValue
            foldChange
            log10FoldChange
            numWildTypes
            numMutants
            dataSet { name }
            feature { name }
            gene { entrez }
            mutationCode
            tag { name }
        }
    }"""


@pytest.fixture(scope='module')
def max_p_value():
    return 0.495103


@pytest.fixture(scope='module')
def max_log10_p_value():
    return 0.197782


@pytest.fixture(scope='module')
def min_fold_change():
    return 1.44142


@pytest.fixture(scope='module')
def min_log10_fold_change():
    return -0.0544383


@pytest.fixture(scope='module')
def min_p_value():
    return 0.634187


@pytest.fixture(scope='module')
def min_log10_p_value():
    return 0.30530497


@pytest.fixture(scope='module')
def min_n_mut():
    return 23


@pytest.fixture(scope='module')
def min_n_wt():
    return 383


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
        tag = result['tag']

        assert current_data_set['name'] == data_set
        assert feature['name'] == feature_name
        assert gene['entrez'] == gene_entrez
        assert type(result['mutationCode']) is str or NoneType
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
        tag = result['tag']

        assert current_data_set['name'] == data_set
        assert feature['name'] == feature_name
        assert gene['entrez'] == gene_entrez
        assert result['mutationCode'] == mutation_code
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
        tag = result['tag']

        assert current_data_set['name'] == data_set
        if feature:
            assert type(feature['name']) is str
        assert gene['entrez'] == gene_entrez
        assert result['mutationCode'] == mutation_code
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
        tag = result['tag']

        assert current_data_set['name'] == data_set
        assert feature['name'] == feature_name
        if gene:
            assert type(gene['entrez']) is int
        assert result['mutationCode'] == mutation_code
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
        tag = result['tag']

        assert type(current_data_set['name']) is str
        assert feature['name'] == feature_name
        assert gene['entrez'] == gene_entrez
        assert result['mutationCode'] == mutation_code
        assert tag['name'] == tag_name


def test_driverResults_query_with_passed_min_p_value(client, common_query, data_set, gene_entrez, feature_name, min_p_value, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'minPValue': min_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] >= min_p_value


def test_driverResults_query_with_passed_min_p_value_and_min_log10_p_value(client, common_query, data_set, gene_entrez, feature_name, min_log10_p_value, min_p_value, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'minLog10PValue': min_log10_p_value,
            'minPValue': min_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] >= min_p_value


def test_driverResults_query_with_passed_min_log10_p_value(client, common_query, data_set, gene_entrez, feature_name, min_log10_p_value, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'minLog10PValue': min_log10_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['log10PValue'] >= min_log10_p_value


def test_driverResults_query_with_passed_max_p_value(client, common_query, data_set, gene_entrez, feature_name, max_p_value, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'maxPValue': max_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] <= max_p_value


def test_driverResults_query_with_passed_max_p_value_and_max_log10_p_value(client, common_query, data_set, gene_entrez, feature_name, max_log10_p_value, max_p_value, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'maxLog10PValue': max_log10_p_value,
            'maxPValue': max_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] <= max_p_value


def test_driverResults_query_with_passed_max_log10_p_value(client, common_query, data_set, gene_entrez, feature_name, max_log10_p_value, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'maxLog10PValue': max_log10_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['log10PValue'] <= max_log10_p_value


def test_driverResults_query_with_passed_min_fold_change(client, common_query, data_set, gene_entrez, feature_name, min_fold_change, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'minFoldChange': min_fold_change,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['foldChange'] >= min_fold_change


def test_driverResults_query_with_passed_min_fold_change_and_min_log10_fold_change(client, common_query, data_set, gene_entrez, feature_name, min_log10_fold_change, min_fold_change, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'maxLog10FoldChange': min_log10_fold_change,
            'minFoldChange': min_fold_change,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['foldChange'] >= min_fold_change


def test_driverResults_query_with_passed_min_log10_fold_change(client, common_query, data_set, gene_entrez, feature_name, min_log10_fold_change, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'minLog10FoldChange': min_log10_fold_change,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['log10FoldChange'] >= min_log10_fold_change


def test_driverResults_query_with_passed_min_n_mut(client, common_query, data_set, gene_entrez, feature_name, min_n_mut, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'minNumMutants': min_n_mut,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['numMutants'] >= min_n_mut


def test_driverResults_query_with_passed_min_n_wt(client, common_query, data_set, gene_entrez, feature_name, min_n_wt, tag_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'entrez': [gene_entrez],
            'feature': [feature_name],
            'minNumWildTypes': min_n_wt,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['driverResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['numWildTypes'] >= min_n_wt

# def test_driverResults_query_with_no_arguments_no_relations(client):
#     query = """query DriverResults(
#         $dataSet: [String!]
#         $entrez: [Int!]
#         $feature: [String!]
#         $mutationCode: [String!]
#         $tag: [String!]
#         $minPValue: Float
#         $maxPValue: Float
#         $minLog10PValue: Float
#         $maxLog10PValue: Float
#         $minFoldChange: Float
#         $minLog10FoldChange: Float
#         $minNumWildTypes: Int
#         $minNumMutants: Int
#     ) {
#         driverResults(
#             dataSet: $dataSet
#             feature: $feature
#             entrez: $entrez
#             mutationCode: $mutationCode
#             tag: $tag
#             minPValue: $minPValue
#             maxPValue: $maxPValue
#             minLog10PValue: $minLog10PValue
#             maxLog10PValue: $maxLog10PValue
#             minFoldChange: $minFoldChange
#             minLog10FoldChange: $minLog10FoldChange
#             minNumWildTypes: $minNumWildTypes
#             minNumMutants: $minNumMutants
#         ) {
#             foldChange
#             pValue
#             log10PValue
#             log10FoldChange
#             numWildTypes
#             numMutants
#         }
#     }"""
#     response = client.post('/api', json={'query': query})
#     json_data = json.loads(response.data)
#     driver_results = json_data['data']['driverResults']
#     assert isinstance(driver_results, list)
#     assert len(driver_results) > 0
#     for driver_result in driver_results[0:2]:
#         assert type(driver_result['foldChange']) is float or NoneType
#         assert type(driver_result['pValue']) is float or NoneType
#         assert type(driver_result['log10PValue']) is float or NoneType
#         assert type(driver_result['log10FoldChange']) is float or NoneType
#         assert type(driver_result['numWildTypes']) is int or NoneType
#         assert type(driver_result['numMutants']) is int or NoneType
