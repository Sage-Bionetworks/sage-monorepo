import json
import pytest
from api.database import return_tag_query
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash


@pytest.fixture(scope='module')
def tag_with_publication():
    return 'BRCA_Normal'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Tags(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $sample: [String!]
            $paging: PagingInput
            $distinct: Boolean
        ) {
            tags(
                dataSet: $dataSet
                related: $related
                tag: $tag
                sample: $sample
                paging: $paging
                distinct: $distinct
        )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def paging_query(common_query_builder):
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
    return(query)


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    query = common_query_builder(
        """
        {
            items {
                id
                characteristics
                color
                longDisplay
                name
                shortDisplay
            }
        }
        """
    )
    return(query)


@pytest.fixture(scope='module')
def full_query(common_query_builder):
    query = common_query_builder(
        """
        {
            items {
                id
                color
                longDisplay
                name
                shortDisplay
                characteristics
                related {
                    name
                    characteristics
                    color
                    longDisplay
                    shortDisplay
                }
                sampleCount
                samples
                publications { name }
                }
        }
        """
    )
    return(query)


def test_tags_cursor_pagination_first(client, paging_query):
    num = 5
    response = client.post(
        '/api', json={'query': paging_query, 'variables': {
            'paging': {'first': num}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
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


def test_tags_cursor_pagination_last(client, paging_query):
    num = 5
    response = client.post(
        '/api', json={'query': paging_query, 'variables': {
            'paging': {
                'last': num,
                'before': to_cursor_hash(1000)
            }
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_tags_cursor_distinct_pagination(client, paging_query):
    page_num = 2
    num = 2
    response = client.post(
        '/api', json={'query': paging_query, 'variables': {
            'paging': {
                'page': page_num,
                'first': num,
            },
            'distinct': True,
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_tags_query_no_args(client, common_query):
    num = 5
    response = client.post(
        '/api',
        json={
            'query': common_query,
            'variables': {'paging': {'first': num}}
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == num
    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['longDisplay']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str


def test_tags_query_with_data_set_related(client, full_query, data_set, related):
    response = client.post(
        '/api',
        json={
            'query': full_query,
            'variables': {
                'dataSet': [data_set],
                'related': [related]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        related = result['related']
        samples = result['samples']
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['longDisplay']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['sampleCount']) is int
        assert isinstance(related, list)
        assert len(related) > 0
        for current_related in related[0:2]:
            assert type(current_related["name"]) is str
            assert type(current_related["characteristics"]) is str or NoneType
            assert type(current_related["color"]) is str or NoneType
            assert type(current_related["longDisplay"]) is str or NoneType
            assert type(current_related["shortDisplay"]) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert type(current_sample) is str


def test_tags_query_no_data_set_and_related(client, common_query, data_set, related):
    response = client.post(
        '/api',
        json={
            'query': common_query,
            'variables': {
                'dataSet': [data_set],
                'related': [related]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str


def test_tags_query_with_data_set_related_and_tag(client, full_query, data_set, related, tag):
    response = client.post(
        '/api',
        json={
            'query': full_query,
            'variables': {
                'dataSet': [data_set],
                'related': [related],
                'tag': [tag]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['name'] == tag
        assert type(result['sampleCount']) is int


def test_tags_query_with_data_set_related_tag_and_sample(client, full_query, data_set, related, tag, sample):
    response = client.post(
        '/api',
        json={
            'query': full_query,
            'variables': {
                'dataSet': [data_set],
                'related': [related],
                'tag': [tag],
                'sample': [sample]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        samples = result['samples']
        assert result['name'] == tag
        assert result['sampleCount'] == 1
        assert isinstance(samples, list)
        assert len(samples) == 1
        for current_sample in samples:
            assert current_sample == sample


def test_tags_query_returns_publications(client, full_query, tag_with_publication):
    response = client.post(
        '/api',
        json={
            'query': full_query,
            'variables': {'tag': [tag_with_publication]}
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        publications = result['publications']
        assert result['name'] == tag_with_publication
        assert isinstance(publications, list)
        assert len(publications) > 0
        for publication in publications[0:5]:
            assert type(publication['name']) is str
