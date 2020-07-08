import json
import pytest
from tests import NoneType


def test_genes_query_with_entrez(client, entrez, hgnc):
    query = """query Genes($entrez: [Int!]) {
        genes(entrez: $entrez) {
            entrez
            hgnc
            geneFamily
            geneFunction
            geneTypes {
                name
                display
            }
            immuneCheckpoint
            pathway
            publications {
                firstAuthorLastName
                journal
                pubmedId
                title
                year
            }
            superCategory
            therapyType
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [entrez]}})
    json_data = json.loads(response.data)
    genes = json_data['data']['genes']

    assert isinstance(genes, list)
    for gene in genes:
        gene_types = gene['geneTypes']
        publications = gene['publications']

        assert gene['entrez'] == entrez
        assert gene['hgnc'] == hgnc
        assert type(gene['geneFamily']) is str or NoneType
        assert type(gene['geneFunction']) is str or NoneType
        assert isinstance(gene_types, list)
        if gene_types:
            for gene_type in gene_types:
                assert type(gene_type['name']) is str
                assert type(gene_type['display']) is str or NoneType
        assert type(gene['immuneCheckpoint']) is str or NoneType
        assert type(gene['pathway']) is str or NoneType
        if publications:
            for publication in publications:
                assert type(
                    publication['firstAuthorLastName']) is str or NoneType
                assert type(publication['journal']) is str or NoneType
                assert type(publication['pubmedId']) is int
                assert type(publication['title']) is str or NoneType
                assert type(publication['year']) is str or NoneType
        assert type(gene['superCategory']) is str or NoneType
        assert type(gene['therapyType']) is str or NoneType


def test_genes_query_no_entrez(client):
    query = """query Genes($entrez: [Int!]) {
        genes(entrez: $entrez) {
            entrez
            hgnc
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    genes = json_data['data']['genes']

    assert isinstance(genes, list)
    for gene in genes[0:1]:
        assert type(gene['entrez']) is int
        assert type(gene['hgnc']) is str
        assert type(gene['geneFunction']) is str or NoneType
