import json
import pytest
from sqlalchemy import and_
from api.database import return_sample_to_mutation_query
from api.enums import status_enum
from api.db_models import DatasetToSample, DatasetToTag, Sample, SampleToTag, Tag
from tests import NoneType


@pytest.fixture(scope='module')
def gene_entrez():
    return 207


@pytest.fixture(scope='module')
def mutation_id():
    return 777


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
        return """query MutationsBySample(
            $dataSet: [String!]
            $entrez: [Int!]
            $mutationCode: [String!]
            $mutationId: [Int!]
            $mutationType: [String!]
            $page: Int
            $related: [String!]
            $sample: [String!]
            $status: [StatusEnum!]
            $tag: [String!]
        ) {
            mutationsBySample(
                dataSet: $dataSet
                entrez: $entrez
                mutationCode: $mutationCode
                mutationId: $mutationId
                mutationType: $mutationType
                page: $page
                related: $related
                sample: $sample
                status: $status
                tag: $tag
            )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder("""{
        items {
            name
            mutations {
                id
                gene { entrez }
                mutationCode
                mutationType { name }
                status
            }
        }
        page
        pages
        total
    }""")


def test_mutations_by_sample_query_with_sample(client, common_query, sample_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'sample': [sample_name]}})
    json_data = json.loads(response.data)
    page = json_data['data']['mutationsBySample']
    results = page['items']

    assert page['page'] == 1
    assert page['pages'] > 0
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        mutations = result['mutations']
        assert result['name'] == sample_name
        assert isinstance(mutations, list)
        assert len(mutations) > 0
        for mutation in mutations[0:2]:
            assert type(mutation['id']) is int
            assert type(mutation['gene']['entrez']) is int
            assert type(mutation['mutationCode']) is str
            assert type(mutation['mutationType']['name']) is str or NoneType
            assert mutation['status'] in status_enum.enums


def test_mutations_by_sample_query_with_mutationId(client, common_query, mutation_id):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'mutationId': [mutation_id]}})
    json_data = json.loads(response.data)
    page = json_data['data']['mutationsBySample']
    results = page['items']

    assert page['page'] == 1
    assert page['pages'] > 0
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        mutations = result['mutations']
        assert type(result['name']) is str
        assert isinstance(mutations, list)
        assert len(mutations) > 0
        for mutation in mutations:
            assert mutation['id'] == mutation_id
            assert type(mutation['gene']['entrez']) is int
            assert type(mutation['mutationCode']) is str
            assert type(mutation['mutationType']['name']) is str or NoneType
            assert mutation['status'] in status_enum.enums


def test_mutations_by_sample_query_with_no_args(client, common_query):
    response = client.post('/api', json={'query': common_query})
    json_data = json.loads(response.data)
    page = json_data['data']['mutationsBySample']
    results = page['items']

    # Get the total number of samples_to_mutations in the database.
    samples_to_mutations_count = return_sample_to_mutation_query(
        'sample_id').count()

    assert page['page'] == 1
    assert page['pages'] > 0
    assert page['total'] == samples_to_mutations_count
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        mutations = result['mutations']
        assert type(result['name']) is str
        assert isinstance(mutations, list)
        assert len(mutations) > 0
        for mutation in mutations[0:2]:
            assert type(mutation['id']) is int
            assert type(mutation['gene']['entrez']) is int
            assert type(mutation['mutationCode']) is str
            assert type(mutation['mutationType']['name']) is str or NoneType
            assert mutation['status'] in status_enum.enums


def test_mutations_by_sample_query_with_status(client, common_query, mutation_status):
    sample_name = 'TCGA-02-0047'
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'sample': [sample_name], 'status': [mutation_status]}})
    json_data = json.loads(response.data)
    page = json_data['data']['mutationsBySample']
    results = page['items']

    assert page['page'] == 1
    assert page['pages'] > 0
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        mutations = result['mutations']
        assert result['name'] == sample_name
        assert isinstance(mutations, list)
        assert len(mutations) > 0
        for mutation in mutations:
            assert type(mutation['id']) is int
            assert type(mutation['gene']['entrez']) is int
            assert type(mutation['mutationCode']) is str
            assert type(mutation['mutationType']['name']) is str or NoneType
            assert mutation['status'] == mutation_status


def test_mutations_by_sample_query_with_mutationId_status_and_sample(client, common_query, mutation_id,
                                                                     mutation_status, sample_name):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'mutationId': [mutation_id],
        'mutationStatus': [mutation_status],
        'sample': [sample_name]}})
    json_data = json.loads(response.data)
    page = json_data['data']['mutationsBySample']
    results = page['items']

    assert page['page'] == 1
    assert page['pages'] > 0
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        mutations = result['mutations']
        assert result['name'] == sample_name
        assert isinstance(mutations, list)
        assert len(mutations) > 0
        for mutation in mutations:
            assert mutation['id'] == mutation_id
            assert type(mutation['gene']['entrez']) is int
            assert type(mutation['mutationCode']) is str
            assert type(mutation['mutationType']['name']) is str or NoneType
            assert mutation['status'] == mutation_status


def test_mutations_by_sample_query_with_entrez(client, common_query_builder, gene_entrez):
    query = common_query_builder("""{
                                    items {
                                        name
                                        mutations {
                                            gene { entrez }
                                        }
                                    }
                                    page
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [gene_entrez]}})
    json_data = json.loads(response.data)
    page = json_data['data']['mutationsBySample']
    results = page['items']

    assert page['page'] == 1
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        mutations = result['mutations']
        assert type(result['name']) is str
        assert isinstance(mutations, list)
        assert len(mutations) > 0
        for mutation in mutations:
            assert mutation['gene']['entrez'] == gene_entrez


def test_mutations_by_sample_query_with_dataSet(client, common_query_builder, data_set, data_set_id, mutation_status, test_db):
    query = common_query_builder("""{
                                    items {
                                        name
                                        mutations { mutationCode }
                                    }
                                    page
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [data_set], 'status': [mutation_status]}})
    json_data = json.loads(response.data)
    page = json_data['data']['mutationsBySample']
    results = page['items']

    sample_name_results = test_db.session.query(Sample.name).select_from(DatasetToSample).filter_by(
        dataset_id=data_set_id).join(Sample, Sample.id == DatasetToSample.sample_id).all()
    sample_names_in_data_set = list(map(lambda s: s.name, sample_name_results))

    assert page['page'] == 1
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        mutations = result['mutations']
        assert type(result['name']) is str
        assert result['name'] in sample_names_in_data_set
        assert isinstance(mutations, list)
        assert len(mutations) > 0
        for mutation in mutations[0:5]:
            assert type(mutation['mutationCode']) is str


def test_mutations_by_sample_query_with_related(client, common_query_builder, data_set, data_set_id, related, related_id, mutation_status, test_db):
    query = common_query_builder("""{
                                    items {
                                        name
                                        mutations { mutationCode }
                                    }
                                    page
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'related': [related],
            'status': [mutation_status]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['mutationsBySample']
    results = page['items']

    sess = test_db.session

    sample_name_query = sess.query(Sample.name).select_from(
        DatasetToSample).filter_by(dataset_id=data_set_id)
    sample_name_query = sample_name_query.join(
        DatasetToTag, and_(DatasetToTag.dataset_id == data_set_id, DatasetToTag.tag_id == related_id))
    sample_name_query = sample_name_query.join(
        Sample, Sample.id == DatasetToSample.sample_id)
    sample_name_results = sample_name_query.all()
    sample_names_in_related = list(map(lambda s: s.name, sample_name_results))

    assert page['page'] == 1
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        mutations = result['mutations']
        assert type(result['name']) is str
        assert result['name'] in sample_names_in_related
        assert isinstance(mutations, list)
        assert len(mutations) > 0
        for mutation in mutations[0:5]:
            assert type(mutation['mutationCode']) is str


def test_mutations_by_sample_query_with_tag(client, common_query_builder, data_set, data_set_id, mutation_status, tag, tag_id, test_db):
    query = common_query_builder("""{
                                    items {
                                        name
                                        mutations { mutationCode }
                                    }
                                    page
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'status': [mutation_status],
            'tag': [tag]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['mutationsBySample']
    results = page['items']

    sess = test_db.session

    sample_name_query = sess.query(Sample.name).select_from(
        DatasetToSample).filter_by(dataset_id=data_set_id)
    sample_name_query = sample_name_query.join(
        SampleToTag, and_(SampleToTag.sample_id == DatasetToSample.sample_id, SampleToTag.tag_id == tag_id))
    sample_name_query = sample_name_query.join(
        Sample, Sample.id == DatasetToSample.sample_id)
    sample_name_results = sample_name_query.all()
    sample_names_in_tags = list(map(lambda s: s.name, sample_name_results))

    assert page['page'] == 1
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        mutations = result['mutations']
        assert type(result['name']) is str
        assert result['name'] in sample_names_in_tags
        assert isinstance(mutations, list)
        assert len(mutations) > 0
        for mutation in mutations[0:5]:
            assert type(mutation['mutationCode']) is str


# def test_mutations_by_sample_query_with_feature(client, common_query_builder, chosen_feature):
#     query = common_query_builder("""{
#                                     items {
#                                         name
#                                         mutations {
#                                             mutationCode
#                                             status
#                                         }
#                                     }
#                                     page
#                                 }""")
#     response = client.post(
#         '/api', json={'query': query, 'variables': {'feature': [chosen_feature]}})
#     json_data = json.loads(response.data)
#     page = json_data['data']['mutationsBySample']
#     results = page['items']

#     assert page['page'] == 1
#     assert isinstance(results, list)
#     assert len(results) > 0
#     for result in results[0:2]:
#         mutations = result['mutations']
#         assert type(result['name']) is str
#         assert isinstance(mutations, list)
#         assert len(mutations) > 0
#         for mutation in mutations:
#             assert (mutation['mutationCode']) is str
#             assert mutation['status'] in status_enum.enums


# def test_mutations_by_sample_query_with_featureClass(client, common_query_builder, feature_class):
#     query = common_query_builder("""{
#                                     items {
#                                         name
#                                         mutations {
#                                             mutationCode
#                                             status
#                                         }
#                                     }
#                                     page
#                                 }""")
#     response = client.post(
#         '/api', json={'query': query, 'variables': {'featureClass': [feature_class]}})
#     json_data = json.loads(response.data)
#     page = json_data['data']['mutationsBySample']
#     results = page['items']

#     assert page['page'] == 1
#     assert isinstance(results, list)
#     assert len(results) > 0
#     for result in results[0:2]:
#         mutations = result['mutations']
#         assert type(result['name']) is str
#         assert isinstance(mutations, list)
#         assert len(mutations) > 0
#         for mutation in mutations:
#             assert (mutation['mutationCode']) is str
#             assert mutation['status'] in status_enum.enums


def test_mutations_by_sample_query_with_sample_and_mutationType(client, common_query_builder, sample_name, mutation_type):
    query = common_query_builder("""{
                                    items {
                                        name
                                        mutations {
                                            mutationType { name }
                                        }
                                    }
                                    page
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'mutationType': [mutation_type],
                                                    'sample': [sample_name]}})
    json_data = json.loads(response.data)
    page = json_data['data']['mutationsBySample']
    results = page['items']

    assert page['page'] == 1
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        mutations = result['mutations']
        assert result['name'] == sample_name
        assert isinstance(mutations, list)
        assert len(mutations) > 0
        for mutation in mutations:
            assert mutation['mutationType']['name'] == mutation_type
