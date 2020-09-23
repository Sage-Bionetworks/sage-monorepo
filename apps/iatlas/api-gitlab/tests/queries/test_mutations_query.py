import json
import pytest
from tests import NoneType


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
            $entrez: [Int!]
            $mutationCode: [String!]
            $mutationId: [Int!]
            $mutationType: [String!]
            $sample: [String!]
            $status: [StatusEnum!]
        ) {
            mutations(
                entrez: $entrez
                mutationCode: $mutationCode
                mutationId: $mutationId
                mutationType: $mutationType
                sample: $sample
                status: $status
            )""" + query_fields + "}"
    return f


def test_mutations_query_with_passed_mutation_id(client, common_query_builder, mutation_id):
    query = common_query_builder("""{ id }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'mutationId': [mutation_id]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) == 1
    for mutation in mutations:
        assert type(mutation['id']) is int
        assert mutation['id'] == mutation_id


def test_mutations_query_with_passed_entrez(client, common_query_builder, gene_entrez):
    query = common_query_builder("""{
                                    id
                                    gene { entrez }
                                    mutationCode
                                    mutationType { name }
                                    samples { name }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [gene_entrez]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) > 0
    for mutation in mutations[0:2]:
        samples = mutation['samples']
        assert type(mutation['id']) is int
        assert mutation['gene']['entrez'] == gene_entrez
        assert type(mutation['mutationCode']) is str
        assert type(mutation['mutationType']['name']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str


def test_mutations_query_with_passed_mutation_code(client, common_query_builder, mutation_code):
    query = common_query_builder("""{
                                    id
                                    mutationCode
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'mutationCode': [mutation_code]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) > 0
    for mutation in mutations[0:2]:
        assert type(mutation['id']) is int
        assert mutation['mutationCode'] == mutation_code


def test_mutations_query_with_passed_mutation_type(client, common_query_builder, mutation_type):
    query = common_query_builder("""{
                                    id
                                    mutationType { name }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'mutationType': [mutation_type]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) > 0
    for mutation in mutations[0:2]:
        assert type(mutation['id']) is int
        assert mutation['mutationType']['name'] == mutation_type


def test_mutations_query_with_passed_sample(client, common_query_builder, sample_name):
    query = common_query_builder("""{
                                    id
                                    samples { name }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'sample': [sample_name]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) > 0
    for mutation in mutations[0:2]:
        samples = mutation['samples']
        assert type(mutation['id']) is int
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['name'] == sample_name


def test_mutations_query_with_passed_sample_and_status(client, common_query_builder, sample_name, mutation_status):
    query = common_query_builder("""{
                                    id
                                    samples {
                                        name
                                        status
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'status': [mutation_status]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) > 0
    for mutation in mutations[0:2]:
        samples = mutation['samples']
        assert type(mutation['id']) is int
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['status'] == mutation_status


def test_mutations_query_with_no_variables(client, common_query_builder):
    query = common_query_builder("""{ id }""")
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) > 0
    for mutation in mutations[0:2]:
        assert type(mutation['id']) is int
