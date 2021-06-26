import json
import pytest
from sqlalchemy import and_
from tests import NoneType
from api.db_models import DatasetToSample, DatasetToTag, Sample, SampleToTag, Tag


@pytest.fixture(scope='module')
def gene_entrez():
    return 92


@pytest.fixture(scope='module')
def mutation_id():
    return 291


@pytest.fixture(scope='module')
def mutation_code():
    return 'G12'


@pytest.fixture(scope='module')
def mutation_status():
    return 'Mut'


# Sample id 1904
@pytest.fixture(scope='module')
def sample_name():
    return 'TCGA-38-7271'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Mutations(
            $paging: PagingInput
            $distinct: Boolean
            $cohort: [String!]
            $entrez: [Int!]
            $mutationCode: [String!]
            $mutationId: [Int!]
            $mutationType: [String!]
            $sample: [String!]
            $status: [StatusEnum!]
        ) {
            mutations(
                paging: $paging
                distinct: $distinct
                cohort: $cohort
                entrez: $entrez
                mutationCode: $mutationCode
                mutationId: $mutationId
                mutationType: $mutationType
                sample: $sample
                status: $status
            )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder("""
        {
            items {
                id
                gene { entrez }
                mutationCode
                mutationType { name }
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
    return common_query_builder("""
        {
            items {
                id
                gene { entrez }
                mutationCode
                mutationType { name }
                samples {
                    name
                    status
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
        }"""
                                )


def test_mutations_query_with_mutationId(client, common_query, mutation_id):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'mutationId': [mutation_id]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']
    page = mutations['items']

    assert isinstance(page, list)
    assert len(page) == 1
    for mutation in page:
        assert mutation['id'] == str(mutation_id)


def test_mutations_query_with_entrez(client, samples_query, gene_entrez):
    response = client.post(
        '/api', json={'query': samples_query, 'variables': {'entrez': [gene_entrez]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']
    page = mutations['items']

    assert isinstance(page, list)
    assert len(page) > 0
    for mutation in page[0:2]:
        samples = mutation['samples']
        assert mutation['gene']['entrez'] == gene_entrez
        assert type(mutation['mutationCode']) is str
        assert type(mutation['mutationType']['name']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str


def test_mutations_query_with_mutationCode(client, common_query, mutation_code):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'mutationCode': [mutation_code]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']
    page = mutations['items']

    assert isinstance(page, list)
    assert len(mutations) > 0
    for mutation in page[0:2]:
        assert mutation['mutationCode'] == mutation_code


def test_mutations_query_with_mutationType(client, common_query, mutation_type):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'mutationType': [mutation_type]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']
    page = mutations['items']

    assert isinstance(page, list)
    assert len(page) > 0
    for mutation in page[0:2]:
        assert mutation['mutationType']['name'] == mutation_type


def test_mutations_query_with_sample(client, samples_query, sample_name):
    response = client.post(
        '/api', json={'query': samples_query, 'variables': {'sample': [sample_name]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']
    page = mutations['items']

    assert isinstance(page, list)
    assert len(page) > 0
    for mutation in page[0:2]:
        samples = mutation['samples']
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['name'] == sample_name


def test_mutations_query_with_sample_and_status(client, samples_query, mutation_status):
    response = client.post(
        '/api', json={'query': samples_query, 'variables': {'paging': {'first': 100}, 'status': [mutation_status]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']
    page = mutations['items']

    assert isinstance(page, list)
    assert len(page) > 0
    for mutation in page[0:2]:
        samples = mutation['samples']
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['status'] == mutation_status


def test_mutations_query_with_no_variables(client, common_query):
    response = client.post(
        '/api', json={'query': common_query})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']
    page = mutations['items']

    assert isinstance(page, list)
    assert len(page) > 0
    for mutation in page[0:2]:
        assert type(mutation['id']) is not None


def test_mutations_query_with_cohort(client, samples_query, tcga_tag_cohort_name, tcga_tag_cohort_samples):
    response = client.post(
        '/api', json={
            'query': samples_query,
            'variables': {
                'paging': {'first': 10},
                'cohort': [tcga_tag_cohort_name]
            }
        }
    )
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']
    page = mutations['items']

    assert isinstance(page, list)
    assert len(page) > 0
    for mutation in page[0:2]:
        samples = mutation['samples']
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str
            assert type(sample['status']) is str
            assert sample['name'] in tcga_tag_cohort_samples
