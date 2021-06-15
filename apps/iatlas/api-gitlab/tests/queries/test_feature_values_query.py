import json
from logging import getLogger
import pytest
from tests import NoneType
from api.enums import unit_enum
#from api.database import return_feature_value_query
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging


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
def cohort_name():
    return 'tcga_immune_subtype'


@pytest.fixture(scope='module')
def cohort_id(test_db, cohort_name):
    from api.db_models import Cohort
    (id, ) = test_db.session.query(Cohort.id).filter_by(
        name=cohort_name).one_or_none()
    return id


@pytest.fixture(scope='module')
def cohort_query_builder():
    def f(query_fields):
        return """
        query Cohorts(
            $paging: PagingInput
            $distinct:Boolean
            $name: [String!]
            $dataSet: [String!]
            $tag: [String!]
            $clinical: [String!]
        ) {
        cohorts(
            paging: $paging
            distinct: $distinct
            name: $name
            dataSet: $dataSet
            tag: $tag
            clinical: $clinical
        )
        """ + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def cohort_query(cohort_query_builder):
    return cohort_query_builder(
        """
        {
            items {
                name
                samples {
                    name
                }
            }
        }
        """
    )


@pytest.fixture(scope='module')
def tcga_cohort_samples(client, cohort_name, cohort_query):
    response = client.post('/api', json={'query': cohort_query, 'variables': {
        'name': [cohort_name]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    cohort = page['items'][0]
    samples = cohort['samples']
    names = [sample['name'] for sample in samples]
    return names


@pytest.fixture(scope='module')
def pcawg_cohort_name():
    return('pcawg_gender')


@pytest.fixture(scope='module')
def pcawg_cohort_samples(client, pcawg_cohort_name, cohort_query):
    response = client.post('/api', json={'query': cohort_query, 'variables': {
        'name': [pcawg_cohort_name]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    cohort = page['items'][0]
    samples = cohort['samples']
    names = [sample['name'] for sample in samples]
    return names


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """
        query FeatureValues(
            $feature: [String!]
            $class: [String!]
            $cohort: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
            $paging: PagingInput
            $distinct: Boolean
        ) {
        featureValues(
            feature: $feature
            class: $class
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
                    unit
                    germlineModule
                    germlineCategory
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
        assert feature_value['feature']['unit'] in unit_enum.enums or type(
            feature_value['feature']['unit']) is NoneType
        assert type(feature_value['feature']
                    ['germlineModule']) is str or NoneType
        assert type(
            feature_value['feature']['germlineCategory']) is str or NoneType


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
