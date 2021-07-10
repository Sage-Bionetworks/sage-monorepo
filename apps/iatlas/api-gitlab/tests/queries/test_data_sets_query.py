import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash


@pytest.fixture(scope='module')
def data_set_type():
    return 'ici'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return(
            """
            query DataSets(
                $paging: PagingInput
                $distinct: Boolean
                $dataSet: [String!]
                $sample: [String!]
                $dataSetType: [String!]
            ) {
                dataSets(
                    paging: $paging
                    distinct: $distinct
                    dataSet: $dataSet
                    sample: $sample
                    dataSetType: $dataSetType
                )
            """ + query_fields + "}"
        )
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder(
        """{
            items {
                id
                display
                name
                type
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
        }"""
    )


@pytest.fixture(scope='module')
def samples_query(common_query_builder):
    return common_query_builder(
        """{
            items {
                id
                display
                name
                type
                samples {
                    name
                }
            }
        }"""
    )


@pytest.fixture(scope='module')
def tags_query(common_query_builder):
    return common_query_builder(
        """{
            items {
                id
                display
                name
                type
                tags {
                    name
                }
            }
        }"""
    )


def test_data_sets_cursor_pagination_first(client, common_query):
    num = 2
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'paging': {'first': num}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['dataSets']
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


def test_data_sets_cursor_pagination_last(client, common_query):
    num = 2
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'paging': {
                'last': num,
                'before': to_cursor_hash(1000)
            }
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['dataSets']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_data_sets_cursor_distinct_pagination(client, common_query):
    page_num = 1
    num = 1
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'paging': {
                'page': page_num,
                'first': num,
            },
            'distinct': True
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['dataSets']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_data_sets_query_no_args(client, common_query):
    response = client.post('/api', json={'query': common_query})
    json_data = json.loads(response.data)
    page = json_data['data']['dataSets']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for data_set in results:
        assert type(data_set['name']) is str
        assert type(data_set['display']) is str or NoneType


def test_data_sets_query_with_dataSet(client, samples_query, data_set):
    response = client.post(
        '/api', json={'query': samples_query, 'variables': {'dataSet': [data_set]}})
    json_data = json.loads(response.data)
    page = json_data['data']['dataSets']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for current_data_set in results:
        samples = current_data_set['samples']

        assert current_data_set['name'] == data_set
        assert type(current_data_set['display']) is str or NoneType
        assert type(current_data_set['type']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        import logging
        logger = logging.getLogger('dataset test')
        logger.info(len(samples))
        for current_sample in samples[0:5]:
            assert type(current_sample['name']) is str


def test_data_sets_query_with_sample(client, samples_query, sample):
    response = client.post(
        '/api', json={'query': samples_query, 'variables': {'sample': [sample]}})
    json_data = json.loads(response.data)
    page = json_data['data']['dataSets']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == 1
    for data_set in results:
        samples = data_set['samples']

        assert type(data_set['name']) is str
        assert type(data_set['display']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) == 1
        if samples:
            for current_sample in samples:
                assert current_sample['name'] == sample


def test_data_sets_query_with_dataSet_and_sample(client, samples_query, data_set, sample):
    response = client.post(
        '/api', json={'query': samples_query, 'variables': {'dataSet': [data_set], 'sample': [sample]}})
    json_data = json.loads(response.data)
    page = json_data['data']['dataSets']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for current_data_set in results:
        samples = current_data_set['samples']

        assert current_data_set['name'] == data_set
        assert type(current_data_set['display']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) == 1
        for current_sample in samples:
            assert current_sample['name'] == sample


def test_data_sets_query_with_dataSetType(client, common_query, data_set_type):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'dataSetType': [data_set_type]}})
    json_data = json.loads(response.data)
    page = json_data['data']['dataSets']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for data_set in results:
        assert type(data_set['name']) is str
        assert type(data_set['display']) is str or NoneType
        assert data_set['type'] == data_set_type


def test_data_sets_query_with_tags(client, tags_query, data_set):
    response = client.post(
        '/api', json={'query': tags_query, 'variables': {'dataSet': [data_set]}})
    json_data = json.loads(response.data)
    page = json_data['data']['dataSets']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == 1
    for d in results:
        assert d['name'] == data_set
        tags = d['tags']
        assert isinstance(tags, list)
        assert len(tags) > 1
        for tag in tags:
            assert type(tag['name']) is str
