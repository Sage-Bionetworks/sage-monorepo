import json
import pytest
from sqlalchemy.sql.expression import false
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging
from api.database import return_cohort_query
import logging


@pytest.fixture(scope='module')
def clinical():
    return "Gender"


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """
        query Cohorts(
            $paging: PagingInput
            $distinct:Boolean
            $cohort: [String!]
            $dataSet: [String!]
            $tag: [String!]
            $clinical: [String!]
        ) {
        cohorts(
            paging: $paging
            distinct: $distinct
            cohort: $cohort
            dataSet: $dataSet
            tag: $tag
            clinical: $clinical
        )
        """ + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder(
        """
    {
        items {
            name
            tag { name }
            dataSet { name }
            clinical
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


@pytest.fixture(scope='module')
def samples_query(common_query_builder):
    return common_query_builder(
        """
        {
            items {
                name
                dataSet { name }
                tag { name }
                clinical
                samples{
                    name
                    clinical_value
                    tag {
                        name
                        shortDisplay
                        longDisplay
                    }
                }
            }
        }
        """)


def test_cohorts_cursor_pagination_first(client, common_query_builder):
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
    page = json_data['data']['cohorts']
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


def test_cohorts_cursor_pagination_last(client, common_query_builder):
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
    page = json_data['data']['cohorts']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_cohorts_cursor_distinct_pagination(client, common_query):
    page_num = 2
    num = 2
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'paging': {
                'page': page_num,
                'first': num,
            },
            'distinct': True,
            'dataSet': ['TCGA']
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_tag_cohort_query_by_name(client, common_query, tcga_tag_cohort_name, data_set, related):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'cohort': [tcga_tag_cohort_name]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == 1
    result = results[0]
    assert result['dataSet']['name'] == data_set
    assert result['tag']['name'] == related
    assert result['name'] == tcga_tag_cohort_name
    assert type(result['clinical']) is NoneType


def test_tag_cohort_query_by_dataset_and_tag(client, common_query, tcga_tag_cohort_name, data_set, related):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
        'tag': [related]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == 1
    result = results[0]
    assert result['dataSet']['name'] == data_set
    assert result['tag']['name'] == related
    assert result['name'] == tcga_tag_cohort_name
    assert type(result['clinical']) is NoneType


def test_clinical_cohort_query_by_name(client, common_query, pcawg_clinical_cohort_name, clinical):
    data_set = "PCAWG"
    response = client.post('/api', json={'query': common_query, 'variables': {
        'cohort': [pcawg_clinical_cohort_name]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == 1
    result = results[0]
    assert result['dataSet']['name'] == data_set
    assert type(result['tag']) is NoneType
    assert result['name'] == pcawg_clinical_cohort_name
    assert result['clinical'] == clinical


def test_clinical_cohort_query_by_dataset_and_tag(client, common_query, pcawg_clinical_cohort_name, clinical):
    data_set = "PCAWG"
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
        'clinical': [clinical]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == 1
    result = results[0]
    assert result['dataSet']['name'] == data_set
    assert type(result['tag']) is NoneType
    assert result['name'] == pcawg_clinical_cohort_name
    assert result['clinical'] == clinical


def test_tag_cohort_samples_query(client, samples_query, tcga_tag_cohort_name, data_set, related):
    response = client.post('/api', json={'query': samples_query, 'variables': {
        'cohort': [tcga_tag_cohort_name]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    result = results[0]
    assert result['name'] == tcga_tag_cohort_name
    assert result['dataSet']['name'] == data_set
    assert result['tag']['name'] == related
    assert type(result['clinical']) is NoneType
    assert isinstance(results, list)
    assert len(results) == 1
    samples = results[0]['samples']
    assert len(samples) > 1
    for sample in samples[0:2]:
        assert type(sample['name'] is str)
        assert type(sample['tag']['name'] is str)
        assert type(sample['clinical_value'] is NoneType)


def test_clinical_cohort_samples_query(client, samples_query, clinical, data_set):
    cohort = "TCGA_Gender"
    response = client.post('/api', json={'query': samples_query, 'variables': {
        'cohort': [cohort]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    result = results[0]
    assert result['name'] == cohort
    assert result['dataSet']['name'] == data_set
    assert type(result['tag']) is NoneType
    assert result['clinical'] == clinical
    assert isinstance(results, list)
    assert len(results) == 1
    samples = results[0]['samples']
    assert len(samples) > 1
    for sample in samples[0:2]:
        assert type(sample['name'] is str)
        assert type(sample['tag'] is NoneType)
        assert type(sample['clinical_value']) is str


def test_dataset_cohort_samples_query(client, samples_query, data_set):
    response = client.post('/api', json={'query': samples_query, 'variables': {
        'cohort': [data_set]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    result = results[0]
    assert result['name'] == data_set
    assert result['dataSet']['name'] == data_set
    assert type(result['tag']) is NoneType
    assert type(result['clinical']) is NoneType
    assert isinstance(results, list)
    assert len(results) == 1
    samples = results[0]['samples']
    assert len(samples) > 1
    for sample in samples[0:2]:
        assert type(sample['name'] is str)
        assert type(sample['tag'] is NoneType)
        assert type(sample['clinical_value'] is NoneType)


def test_tag_cohort_features_query(client, common_query_builder, tcga_tag_cohort_name, data_set, related):
    query = common_query_builder(
        """
        {
            items {
                name
                tag { name }
                dataSet { name }
                clinical
                features {
                    name
                    display
                }
            }
        }
        """)
    response = client.post('/api', json={'query': query, 'variables': {
        'cohort': [tcga_tag_cohort_name]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    result = results[0]
    assert result['dataSet']['name'] == data_set
    assert result['tag']['name'] == related
    assert result['name'] == tcga_tag_cohort_name
    assert type(result['clinical']) is NoneType
    assert isinstance(results, list)
    assert len(results) == 1
    features = results[0]['features']
    assert len(features) > 1
    for feature in features[0:2]:
        assert type(feature['name'] is str)
        assert type(feature['display'] is str)


def test_tag_cohort_genes_query(client, common_query_builder, tcga_tag_cohort_name, data_set, related):
    query = common_query_builder(
        """
        {
            items {
                name
                tag { name }
                dataSet { name }
                clinical
                genes {
                    hgnc
                    entrez
                }
            }
        }
        """)
    response = client.post('/api', json={'query': query, 'variables': {
        'cohort': [tcga_tag_cohort_name]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    result = results[0]
    assert result['dataSet']['name'] == data_set
    assert result['tag']['name'] == related
    assert result['name'] == tcga_tag_cohort_name
    assert type(result['clinical']) is NoneType
    assert isinstance(results, list)
    assert len(results) == 1
    genes = results[0]['genes']
    assert len(genes) > 1
    for gene in genes[0:2]:
        assert type(gene['hgnc'] is str)
        assert type(gene['entrez'] is int)


def test_tag_cohort_mutations_query(client, common_query_builder, tcga_tag_cohort_name, data_set, related):
    query = common_query_builder(
        """
        {
            items {
                name
                tag { name }
                dataSet { name }
                clinical
                mutations {
                    mutationCode
                    gene {
                        entrez
                        hgnc
                    }
                }
            }
        }
        """
    )
    response = client.post('/api', json={'query': query, 'variables': {
        'cohort': [tcga_tag_cohort_name]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    result = results[0]
    assert result['dataSet']['name'] == data_set
    assert result['tag']['name'] == related
    assert result['name'] == tcga_tag_cohort_name
    assert type(result['clinical']) is NoneType
    assert isinstance(results, list)
    assert len(results) == 1
    mutations = results[0]['mutations']
    assert len(mutations) > 1
    for mutation in mutations[0:2]:
        assert type(mutation['mutationCode'] is str)
        assert type(mutation['gene']['hgnc'] is str)
        assert type(mutation['gene']['entrez'] is int)
