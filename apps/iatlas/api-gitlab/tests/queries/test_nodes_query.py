import json
import pytest
from api.database import return_node_query
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import Paging


@pytest.fixture(scope='module')
def node_feature():
    return 'B_cells_Aggregate2'


@pytest.fixture(scope='module')
def node_feature_class():
    return 'Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class'


@pytest.fixture(scope='module')
def node_gene_type():
    return 'Chemokine12_score'


@pytest.fixture(scope='module')
def max_score():
    return 0.0234375


@pytest.fixture(scope='module')
def min_score():
    return 0.9980582524271844891


@pytest.fixture(scope='module')
def network():
    return 'Extracellular Network'

@pytest.fixture(scope='module')
def node_entrez_id():
    return 5797


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Nodes(
            $paging: PagingInput
            $distinct: Boolean
            $dataSet: [String!]
            $entrez: [Int!]
            $feature: [String!]
            $featureClass: [String!]
            $geneType: [String!]
            $maxScore: Float
            $minScore: Float
            $network: [String!]
            $related: [String!]
            $tag1: [String!]
            $tag2: [String!]
            $nTags: Int
        ) {
            nodes(
                paging: $paging
                distinct: $distinct
                dataSet: $dataSet
                entrez: $entrez
                feature: $feature
                featureClass: $featureClass
                geneType: $geneType
                maxScore: $maxScore
                minScore: $minScore
                network: $network
                related: $related
                tag1: $tag1
                tag2: $tag2
                nTags: $nTags
            )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder("""{
        items { name }
        paging {
            page
            pages
            total
            returned
        }
    }""")


@pytest.fixture(scope='module')
def genes_query(common_query_builder):
    return common_query_builder(
    """{
         items{
            label
            name
            score
            x
            y
            tags {
                characteristics
                color
                name
                longDisplay
                order
                shortDisplay
                type
            }
            gene {
                entrez
                hgnc
                friendlyName
            }
        }
        paging {
            type
            pages
            total
            page
            limit
            hasNextPage
            hasPreviousPage
            startCursor
            endCursor
        }
        error
    }""")


def test_nodes_query_with_passed_data_set(client, common_query, data_set):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'dataSet': [data_set]}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']
    paging = page['paging']

    assert type(paging['page']) is NoneType
    assert type(paging['pages']) is int
    assert type(paging['total']) is int
    assert type(paging['returned']) is int
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str


def test_nodes_query_with_passed_data_set_page2(client, common_query_builder, data_set):

    query = common_query_builder("""{
                                    items { name }
                                    paging {
                                        endCursor
                                        startCursor
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [data_set], "paging": {"limit": 10}}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']
    paging = page['paging']
    assert type(paging['endCursor']) is str
    assert type(paging['startCursor']) is str
    assert isinstance(results, list)
    assert len(results) == 10
    for result in results[0:2]:
        assert type(result['name']) is str

    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [data_set], "paging": {"limit": 10, "after": paging['endCursor']}}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']
    paging = page['paging']
    assert type(paging['startCursor']) is str
    assert type(paging['endCursor']) is str
    assert isinstance(results, list)
    assert len(results) == 10
    for result in results[0:2]:
        assert type(result['name']) is str


def test_nodes_query_with_passed_data_set_offset(client, common_query_builder, data_set):
    query = common_query_builder("""{
                                    items { name }
                                    paging {
                                        page
                                        pages
                                        total
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'paging': {'type': Paging.OFFSET, 'page': 2}, 'dataSet': [data_set], }})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']
    paging = page['paging']

    assert paging['page'] == 2
    assert type(paging['pages']) is int
    assert type(paging['total']) is int
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str


def test_nodes_query_with_passed_related(client, common_query_builder, related):
    query = common_query_builder("""{
                                    items {
                                        name
                                        gene { entrez }
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'related': [related]}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        gene = result['gene']
        assert type(result['name']) is str
        if gene:
            assert type(gene['entrez']) is int


def test_nodes_query_with_passed_entrez(client, common_query_builder, node_entrez_id):
    query = common_query_builder("""{
                                    items {
                                        name
                                        gene { entrez }
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'entrez': [node_entrez_id]}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        gene = result['gene']
        assert type(result['name']) is str
        assert gene['entrez'] == node_entrez_id


def test_nodes_query_with_passed_feature(client, common_query_builder, node_feature):
    query = common_query_builder("""{
                                    items {
                                        name
                                        feature { name }
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'feature': [node_feature]}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        feature = result['feature']
        assert type(result['name']) is str
        assert feature['name'] == node_feature


def test_nodes_query_with_passed_featureClass(client, common_query_builder, node_feature_class):
    query = common_query_builder("""{
                                    items {
                                        name
                                        feature { name }
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'featureClass': ['does_not_exist']}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 0

    response = client.post('/api', json={'query': query,
                                         'variables': {'featureClass': [node_feature_class]}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        feature = result['feature']
        assert type(result['name']) is str
        assert type(feature['name']) is str


def test_nodes_query_with_passed_geneType(client, common_query_builder, node_gene_type):
    query = common_query_builder("""{
                                    items {
                                        name
                                        gene { entrez }
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'geneType': ['does_not_exist']}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 0

    response = client.post('/api', json={'query': query,
                                         'variables': {'geneType': [node_gene_type]}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        gene = result['gene']
        assert type(result['name']) is str
        assert type(gene['entrez']) is int


def test_nodes_query_with_passed_network(client, common_query_builder, network):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                        x
                                        y
                                        network
                                        feature { name }
                                        tag1 { name }
                                    }
                                }""")
    num = 200
    response = client.post('/api', json={'query': query,
                                         'variables': {
                                             'network': [network],
                                             'paging': {'first': num}
                                         }
                                         })
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == num
    for result in results[0:2]:
        feature = result['feature']
        assert result['network'] == network
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['score']) is float or NoneType
        assert type(result['x']) is float or NoneType
        assert type(result['y']) is float or NoneType
        assert type(result['tag1']['name']) is str
        if feature:
            assert type(feature['name']) is str


def test_nodes_query_with_passed_network_and_tag(client, common_query_builder, network, tag):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                        x
                                        y
                                        network
                                        gene { entrez }
                                        tag1 { name }
                                    }
                                }""")
    num = 1000
    response = client.post('/api', json={'query': query,
                                         'variables': {
                                             'network': [network],
                                             'tag1': [tag],
                                             'paging': {'first': num}
                                         }
                                         })
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        gene = result['gene']
        assert result['network'] == network
        assert result['tag1']['name'] == tag
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['score']) is float or NoneType
        assert type(result['x']) is float or NoneType
        assert type(result['y']) is float or NoneType
        if gene:
            assert type(gene['entrez']) is int


def test_nodes_query_with_passed_tag(client, common_query_builder, tag):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        tag1 {
                                            name
                                            characteristics
                                            color
                                            longDisplay
                                            shortDisplay
                                        }
                                    }
                                }""")
    num = 100
    response = client.post('/api', json={
        'query': query,
        'variables': {'tag1': [tag], 'paging': {'first': num}}
    })
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == num
    for result in results[0:2]:
        tag1 = result['tag1']
        assert tag1['name'] == tag


def test_nodes_query_with_passed_tag_and_entrez(client, common_query_builder, node_entrez_id, tag):
    query = common_query_builder("""{
                                    items {
                                        name
                                        gene { entrez }
                                        tag1 { name }
                                    }
                                }""")
    response = client.post(
        '/api',
        json={
            'query': query,
            'variables': {'entrez': [node_entrez_id], 'tag1': [tag]}
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        gene = result['gene']
        assert gene['entrez'] == node_entrez_id
        tag1 = result['tag1']
        assert tag1['name'] == tag


def test_nodes_query_with_passed_tag_and_feature(client, common_query_builder, node_feature, tag):
    query = common_query_builder(""" {
        items {
            name
            feature { name }
            tag1 { name }
        }
    } """)
    response = client.post(
        '/api',
        json={
            'query': query,
            'variables': {
                'feature': [node_feature],
                'tag1': [tag]
            }
        }

    )
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        feature = result['feature']
        assert feature['name'] == node_feature
        tag1 = result['tag1']
        assert tag1['name'] == tag


def test_nodes_query_with_passed_tag1_and_tag2(client, common_query_builder, node_feature, tag):
    query = common_query_builder(""" {
        items {
            name
            tag1 { name }
            tag2 { name }
        }
    } """)

    response = client.post(
        '/api',
        json={
            'query': query,
            'variables': {
                'tag1': ['C3'],
                'tag2': ['ACC']
            }
        }
    )

    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['tag1']['name'] == 'C3'
        assert result['tag2']['name'] == 'ACC'


def test_nodes_query_with_passed_maxScore(client, common_query_builder, max_score):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'maxScore': max_score}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert result['score'] <= max_score


def test_nodes_query_with_passed_minScore(client, common_query_builder, min_score):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'minScore': min_score}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert result['score'] >= min_score


def test_nodes_query_with_passed_maxScore_and_minScore(client, common_query_builder, max_score):
    min_score = 0.015625
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'maxScore': max_score, 'minScore': min_score}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert result['score'] <= max_score
        assert result['score'] >= min_score


def test_nodes_query_with_no_arguments(client, common_query_builder):
    query = common_query_builder("""{
                                    items {
                                        name
                                        dataSet { name }
                                    }
                                    paging {
                                        total
                                    }
                                }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']
    paging = page['paging']

    # Get the total number of samples_to_mutations in the database.
    node_count = return_node_query('id').count()

    assert paging['total'] == node_count
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        assert type(result['name']) is str
        assert type(current_data_set['name']) is str


def test_nodes_query_with_ntags(client, common_query_builder):
    query = common_query_builder(""" {
        items {
            name
            tag1 { name }
            tag2 { name }
        }
    } """)

    response = client.post(
        '/api',
        json={
            'query': query,
            'variables': {
                'dataSet': ['PCAWG'],
                'entrez': [4909],
                'tag1': ['C3'],
                'nTags': 1
            }
        }
    )

    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    result = results[0]
    assert result['name'] == 'PCAWG_extracellular_network_C3_4909'
    assert result['tag1']['name'] == 'C3'
    assert result['tag2'] is None

    response2 = client.post(
        '/api',
        json={
            'query': query,
            'variables': {
                'tag1': ['C3'],
                'tag2': ['BLCA'],
                'entrez': [651],
            }
        }
    )

    json_data2 = json.loads(response2.data)
    page2 = json_data2['data']['nodes']
    results2 = page2['items']

    assert isinstance(results2, list)
    assert len(results2) == 1
    result2 = results2[0]
    assert result2['name'] == 'TCGA_extracellular_network_C3:BLCA_651'
    assert result2['tag1']['name'] == 'C3'
    assert result2['tag2']['name'] == 'BLCA'

def test_tag1_no_ntags(client, common_query_builder):
    query = common_query_builder("""{
                                    items {
                                        name
                                        network
                                        dataSet {
                                            name
                                        }
                                        gene{
                                            entrez
                                        }
                                        tag1 {
                                            name
                                        }
                                        tag2 {
                                            name
                                        }
                                    }
                                }""")

    response = client.post('/api', json={
        'query': query,
        'variables': {
            'tag1': ['C1'],
            'dataSet': ['TCGA'],
            'entrez': [2],
            'network': "Extracellular Network"
        }
    })
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    for result in results:
        assert result['tag1']['name'] == "C1"
        assert result['dataSet']['name'] == "TCGA"
        assert result['gene']['entrez'] == 2
        assert result['network'] == "Extracellular Network"
