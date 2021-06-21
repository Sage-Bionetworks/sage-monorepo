import json
from logging import getLogger
import pytest
from tests import NoneType
from api.enums import unit_enum
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash


@pytest.fixture(scope='module')
def feature_name():
    return 'Eosinophils'


@pytest.fixture(scope='module')
def germline_category():
    return 'Leukocyte Subset ES'


@pytest.fixture(scope='module')
def germline_module():
    return 'Cytotoxic'


@pytest.fixture(scope='module')
def max_value():
    return 5.7561021


@pytest.fixture(scope='module')
def min_value():
    return 0.094192693


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """
        query FeatureValues(
            $feature: [String!]
            $featureClass: [String!]
            $cohort: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
            $paging: PagingInput
            $distinct: Boolean
        ) {
        featureValues(
            feature: $feature
            featureClass: $featureClass
            cohort: $cohort
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
            paging: $paging
            distinct: $distinct
            )
        """ + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder(
        """
        {
            items {
                value
                sample { name }
                feature {
                    name
                    display
                    order
                    class
                }
            }
            paging {
                type
                pages
                total
                startCursor
                endCursor
                hasPreviousPage
                hasNextPage
                page
                limit
            }
            error
        }
        """
    )


def test_feature_values_cursor_pagination_first(client, common_query_builder):
    query = common_query_builder("""{
            items {
                id
            }
            paging {
                type
                pages
                total
                startCursor
                endCursor
                hasPreviousPage
                hasNextPage
                page
                limit
            }
        }""")
    num = 5
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {'first': num}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['featureValues']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == True
    assert paging['hasPreviousPage'] == False
    assert start == items[0]['id']
    assert end == items[num - 1]['id']
    assert int(end) - int(start) > 0


def test_feature_values_cursor_pagination_last(client, common_query_builder):
    query = common_query_builder("""{
            items {
                id
            }
            paging {
                type
                pages
                total
                startCursor
                endCursor
                hasPreviousPage
                hasNextPage
                page
                limit
            }
        }""")
    num = 5
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {
                'last': num,
                'before': to_cursor_hash(1000)
            }
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['featureValues']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_feature_values_cursor_distinct_pagination(client, common_query):
    page_num = 2
    num = 2
    response = client.post(
        '/api', json={
            'query': common_query,
            'variables': {
                'paging': {
                    'page': page_num,
                    'first': num
                },
                'distinct': True
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['featureValues']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_feature_values_query_with_no_args(client, common_query):
    num = 10
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'paging': {'first': num}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['featureValues']
    feature_values = page['items']

    assert isinstance(feature_values, list)
    assert len(feature_values) == num
    for feature_value in feature_values:
        assert type(feature_value['value']) is float
        assert type(feature_value['sample']['name']) is str
        assert type(feature_value['feature']['name']) is str
        assert type(feature_value['feature']['display']) is str
        assert type(feature_value['feature']['order']) is int or NoneType
        assert type(feature_value['feature']['class']) is str


def test_feature_values_query_with_feature(client, chosen_feature, common_query):
    num = 10
    response = client.post(
        '/api', json={
            'query': common_query,
            'variables': {
                'feature': [chosen_feature],
                'paging': {'first': num}
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['featureValues']
    feature_values = page['items']

    assert isinstance(feature_values, list)
    assert len(feature_values) == num
    for feature_value in feature_values:
        assert feature_value['feature']['name'] == chosen_feature


def test_feature_values_query_with_class(client, common_query, feature_class2, data_set, feature_class2_feature_names):
    num = 100000
    response = client.post(
        '/api', json={
            'query': common_query,
            'variables': {
                'featureClass': [feature_class2],
                'cohort': [data_set],
                'paging': {'first': num}
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['featureValues']
    featureValues = page['items']

    assert isinstance(featureValues, list)
    assert len(featureValues) == num
    for featureValue in featureValues[0:10]:
        assert type(featureValue['feature']['name']) is str
        assert featureValue['feature']['name'] in feature_class2_feature_names
        assert type(featureValue['feature']['display']) is str
        assert type(featureValue['sample']['name']) is str
        assert type(featureValue['value']) is float
