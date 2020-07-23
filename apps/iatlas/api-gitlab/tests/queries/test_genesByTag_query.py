import json
import pytest
from tests import NoneType
from api.database import return_feature_class_query


@pytest.fixture(scope='module')
def gene_type():
    return 'extra_cellular_network'


def test_genesByTag_query_with_entrez(client, data_set, related, entrez, hgnc):
    query = """query GenesByTag(
        $dataSet: [String!]!
        $related: [String!]!
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $entrez: [Int!]
        $geneType: [String!]
    ) {
        genesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            entrez: $entrez
            geneType: $geneType
        ) {
            tag
            characteristics
            display
            genes {
                entrez
                hgnc
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'entrez': [entrez]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        genes = result['genes']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['display']) is str or NoneType
        assert isinstance(genes, list)
        assert len(genes) == 1
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            assert gene['entrez'] == entrez
            assert gene['hgnc'] == hgnc


# def test_genesByTag_query_no_entrez(client, data_set, related, tag):
#     query = """query GenesByTag(
#         $dataSet: [String!]!
#         $related: [String!]!
#         $tag: [String!]
#         $feature: [String!]
#         $featureClass: [String!]
#         $entrez: [Int!]
#         $geneType: [String!]
#     ) {
#         genesByTag(
#             dataSet: $dataSet
#             related: $related
#             tag: $tag
#             feature: $feature
#             featureClass: $featureClass
#             entrez: $entrez
#             geneType: $geneType
#         ) {
#             tag
#             genes { entrez }
#         }
#     }"""
#     response = client.post(
#         '/api', json={'query': query,
#                       'variables': {'dataSet': [data_set],
#                                     'related': [related],
#                                     'tag': [tag]}})
#     json_data = json.loads(response.data)
#     results = json_data['data']['genesByTag']

#     assert isinstance(results, list)
#     assert len(results) == 1
#     for result in results:
#         genes = result['genes']
#         assert result['tag'] == tag
#         assert isinstance(genes, list)
#         assert len(genes) > 0
#         # Don't need to iterate through every result.
#         for gene in genes[0:2]:
#             assert type(gene['entrez']) is int


def test_genesByTag_query_no_relations(client, data_set, related, entrez, hgnc):
    query = """query GenesByTag(
        $dataSet: [String!]!
        $related: [String!]!
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $entrez: [Int!]
        $geneType: [String!]
    ) {
        genesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            entrez: $entrez
            geneType: $geneType
        ) {
            tag
            characteristics
            display
            genes {
                entrez
                hgnc
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'entrez': [entrez]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        genes = result['genes']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['display']) is str or NoneType
        assert isinstance(genes, list)
        assert len(genes) == 1
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            assert gene['entrez'] == entrez
            assert gene['hgnc'] == hgnc


def test_genesByTag_query_with_gene_type(client, data_set, related, entrez, hgnc, gene_type):
    query = """query GenesByTag(
        $dataSet: [String!]!
        $related: [String!]!
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $entrez: [Int!]
        $geneType: [String!]
    ) {
        genesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            entrez: $entrez
            geneType: $geneType
        ) {
            tag
            characteristics
            display
            genes {
                entrez
                hgnc
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'entrez': [entrez],
                                    'geneType': [gene_type]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        genes = result['genes']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['display']) is str or NoneType
        assert isinstance(genes, list)
        assert len(genes) == 1
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            assert gene['entrez'] == entrez
            assert gene['hgnc'] == hgnc
