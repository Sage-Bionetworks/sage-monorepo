import json
import pytest
from tests import NoneType
from api.enums import direction_enum


@pytest.fixture(scope='module')
def feature_name():
    return 'frac_altered'


@pytest.fixture(scope='module')
def tag_name():
    return 'BLCA'


@pytest.fixture(scope='module')
def direction():
    return 'Amp'


@pytest.fixture(scope='module')
def min_p_value():
    return 0.0000028


@pytest.fixture(scope='module')
def max_p_value():
    return 0.000021


@pytest.fixture(scope='module')
def min_log10_p_value():
    return 0.000037


@pytest.fixture(scope='module')
def max_log10_p_value():
    return 13.162428


@pytest.fixture(scope='module')
def min_mean_normal():
    return 9.313083


@pytest.fixture(scope='module')
def min_mean_cnv():
    return 14.833332


@pytest.fixture(scope='module')
def min_t_stat():
    return -5.118745


def test_copyNumberResults_query_with_passed_data_set(client, data_set, entrez, feature_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            dataSet { name }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'feature_name': [feature_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        assert current_data_set['name'] == data_set


def test_copyNumberResults_query_with_passed_entrez(client, data_set, entrez, feature_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            gene { entrez }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'feature': [feature_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        gene = result['gene']
        assert gene['entrez'] == entrez


def test_copyNumberResults_query_with_passed_features(client, data_set, entrez, feature_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            feature { name }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'feature': [feature_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        feature = result['feature']
        assert feature['name'] == feature_name


def test_copyNumberResults_query_with_passed_tag(client, data_set, entrez, feature_name, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            tag { name }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'feature': [feature_name],
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        tag = result['tag']
        assert tag['name'] == tag_name


def test_copyNumberResults_query_with_passed_direction(client, data_set, direction, entrez, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            direction
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'direction': direction,
            'entrez': [entrez],
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['direction'] == direction


def test_copyNumberResults_query_with_passed_min_p_value(client, data_set, entrez, min_p_value, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            pValue
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minPValue': min_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] >= min_p_value


def test_copyNumberResults_query_with_passed_min_p_value_and_min_log10_p_value(client, data_set, entrez, min_log10_p_value, min_p_value, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            pValue
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minLog10PValue': min_log10_p_value,
            'minPValue': min_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] >= min_p_value


def test_copyNumberResults_query_with_passed_max_p_value(client, data_set, entrez, max_p_value, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            pValue
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'maxPValue': max_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] <= max_p_value


def test_copyNumberResults_query_with_passed_max_p_value_and_max_log10_p_value(client, data_set, entrez, max_log10_p_value, max_p_value, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            pValue
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'maxLog10PValue': max_log10_p_value,
            'maxPValue': max_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] <= max_p_value


def test_copyNumberResults_query_with_passed_min_log10_p_value(client, data_set, entrez, min_log10_p_value, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            log10PValue
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minLog10PValue': min_log10_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['log10PValue'] >= min_log10_p_value


def test_copyNumberResults_query_with_passed_max_log10_p_value(client, data_set, entrez, max_log10_p_value, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            log10PValue
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'maxLog10PValue': max_log10_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['log10PValue'] <= max_log10_p_value


def test_copyNumberResults_query_with_passed_min_mean_normal(client, data_set, entrez, min_mean_normal, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            meanNormal
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minMeanNormal': min_mean_normal,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['meanNormal'] >= min_mean_normal


def test_copyNumberResults_query_with_passed_min_mean_cnv(client, data_set, entrez, min_mean_cnv, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            meanCnv
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minMeanCnv': min_mean_cnv,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['meanCnv'] >= min_mean_cnv


def test_copyNumberResults_query_with_passed_min_t_stat(client, data_set, entrez, min_t_stat, tag_name):
    query = """query CopyNumberResults(
        $dataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $tag: [String!]
        $direction: DirectionEnum
        $minPValue: Float
        $maxPValue: Float
        $minLog10PValue: Float
        $maxLog10PValue: Float
        $minMeanNormal: Float
        $minMeanCnv: Float
        $minTStat: Float
    ) {
        copyNumberResults(
            dataSet: $dataSet
            feature: $feature
            entrez: $entrez
            tag: $tag
            direction: $direction
            minPValue: $minPValue
            maxPValue: $maxPValue
            minLog10PValue: $minLog10PValue
            maxLog10PValue: $maxLog10PValue
            minMeanNormal: $minMeanNormal
            minMeanCnv: $minMeanCnv
            minTStat: $minTStat
        ) {
            tStat
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minTStat': min_t_stat,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['copyNumberResults']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['tStat'] >= min_t_stat


# def test_copyNumberResults_query_with_no_arguments(client):
#     query = """query CopyNumberResults(
#         $dataSet: [String!]
#         $feature: [String!]
#         $entrez: [Int!]
#         $tag: [String!]
#         $direction: DirectionEnum
#         $minPValue: Float
#         $maxPValue: Float
#         $minLog10PValue: Float
#         $maxLog10PValue: Float
#         $minMeanNormal: Float
#         $minMeanCnv: Float
#         $minTStat: Float
#     ) {
#         copyNumberResults(
#             dataSet: $dataSet
#             feature: $feature
#             entrez: $entrez
#             tag: $tag
#             direction: $direction
#             minPValue: $minPValue
#             maxPValue: $maxPValue
#             minLog10PValue: $minLog10PValue
#             maxLog10PValue: $maxLog10PValue
#             minMeanNormal: $minMeanNormal
#             minMeanCnv: $minMeanCnv
#             minTStat: $minTStat
#         ) {
#             direction
#             meanNormal
#             meanCnv
#             pValue
#             log10PValue
#             tStat
#         }
#     }"""
#     response = client.post('/api', json={'query': query})
#     json_data = json.loads(response.data)
#     results = json_data['data']['copyNumberResults']
#     assert isinstance(results, list)
#     assert len(results) > 0
#     for result in results[0:2]:
#         assert result['direction'] in direction_enum.enums
#         assert type(result['meanNormal']) is float or NoneType
#         assert type(result['meanCnv']) is float or NoneType
#         assert type(result['pValue']) is float or NoneType
#         assert type(result['log10PValue']) is float or NoneType
#         assert type(result['tStat']) is int or NoneType
