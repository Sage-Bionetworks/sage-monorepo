import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash


@pytest.fixture(scope='module')
def tag_with_publication():
    return 'BRCA_Normal'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Tags(
            $cohort: [String!]
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $sample: [String!]
            $paging: PagingInput
            $distinct: Boolean
        ) {
            tags(
                cohort: $cohort
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
def samples_query(common_query_builder):
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
                samples { name }
            }
        }
        """
    )
    return(query)


@pytest.fixture(scope='module')
def related_query(common_query_builder):
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
                related {
                    name
                    characteristics
                    color
                    longDisplay
                    shortDisplay
                }
            }
        }
        """
    )
    return(query)


@pytest.fixture(scope='module')
def publications_query(common_query_builder):
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
                publications {
                    name
                    firstAuthorLastName
                    doId
                    journal
                    pubmedId
                    title
                    year
                }
            }
        }
        """
    )
    return(query)


@pytest.fixture(scope='module')
def sample_count_query(common_query_builder):
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
                sampleCount
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


@pytest.fixture(scope='module')
def full_query2(common_query_builder):
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
        assert 'sampleCount' not in result
        assert 'samples' not in result
        assert 'related' not in result
        assert 'publications' not in result


def test_tags_query_with_data_set(client, common_query, data_set):
    response = client.post(
        '/api',
        json={
            'query': common_query,
            'variables': {
                'dataSet': [data_set]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 6
    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['longDisplay']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str


def test_tags_query_with_cohort(client, samples_query, tcga_tag_cohort_name, tcga_tag_cohort_samples):
    response = client.post(
        '/api',
        json={
            'query': samples_query,
            'variables': {
                'cohort': [tcga_tag_cohort_name]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 1
    for result in results[0:3]:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['longDisplay']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str
        samples = result['samples']
        assert 'sampleCount' not in result
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str
            assert sample['name'] in tcga_tag_cohort_samples


def test_tags_query_with_related(client, related_query, related):
    response = client.post(
        '/api',
        json={
            'query': related_query,
            'variables': {
                'related': [related]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 6
    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['longDisplay']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str
        assert result['name'] in ["C1", "C2", "C3", "C4", "C5", "C6"]
        tags = result['related']
        assert isinstance(tags, list)
        assert len(tags) == 2
        for tag in tags:
            assert type(tag['characteristics']) is str or NoneType
            assert type(tag['color']) is str or NoneType
            assert type(tag['longDisplay']) is str or NoneType
            assert type(tag['shortDisplay']) is str or NoneType
            assert type(tag['name']) is str
            assert tag['name'] in [related, 'group']


def test_tags_query_with_related2(client, related_query, related2):
    response = client.post(
        '/api',
        json={
            'query': related_query,
            'variables': {
                'related': [related2]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 2

    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['longDisplay']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str
        assert result['name'] in ["male", "female"]
        tags = result['related']
        assert isinstance(tags, list)
        assert len(tags) == 2
        for tag in tags:
            assert type(tag['characteristics']) is str or NoneType
            assert type(tag['color']) is str or NoneType
            assert type(tag['longDisplay']) is str or NoneType
            assert type(tag['shortDisplay']) is str or NoneType
            assert type(tag['name']) is str
            assert tag['name'] in [related2, 'group']


def test_tags_query_with_sample(client, sample_count_query, sample):
    response = client.post(
        '/api',
        json={
            'query': sample_count_query,
            'variables': {
                'sample': [sample]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['tags']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 1

    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['longDisplay']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str
        assert result['sampleCount'] == 1
        assert 'samples' not in result


def test_tags_query_returns_publications(client, publications_query, tag_with_publication):
    response = client.post(
        '/api',
        json={
            'query': publications_query,
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
