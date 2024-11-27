import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging
from api.db_models import Neoantigen, Gene, Patient


@pytest.fixture(scope='module')
def test_neoantigen(test_db):
    query = test_db.session.query(
        Neoantigen.id,
        Neoantigen.patient_id,
        Neoantigen.neoantigen_gene_id,
        Neoantigen.pmhc,
        Neoantigen.freq_pmhc,
        Neoantigen.tpm
    )
    query = query.filter(Neoantigen.neoantigen_gene_id.isnot(None)).limit(1)
    return query.one_or_none()


@pytest.fixture(scope='module')
def test_gene(test_db, test_neoantigen):
    query = test_db.session.query(
        Gene.id,
        Gene.entrez_id,
        Gene.hgnc_id
    )
    query = query.filter_by(id=test_neoantigen.neoantigen_gene_id)
    return query.one_or_none()


@pytest.fixture(scope='module')
def test_patient(test_db, test_neoantigen):
    query = test_db.session.query(
        Patient.id,
        Patient.name
    )
    query = query.filter_by(id=test_neoantigen.patient_id)
    return query.one_or_none()


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Neoantigens(
            $paging: PagingInput
            $distinct:Boolean
            $entrez: [Int!]
            $patient: [String!]
            $pmhc: [String!]
    ) {
        neoantigens(
            paging: $paging
            distinct: $distinct
            entrez: $entrez
            patient: $patient
            pmhc: $pmhc
        )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder("""{
            items {
              id
              tpm
              pmhc
              freqPmhc
              patient { barcode }
              gene {
                entrez
                hgnc
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
        }""")


def test_cursor_pagination_first(client, common_query_builder):
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
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {'first': num}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['neoantigens']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == True
    assert paging['hasPreviousPage'] == False
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_cursor_pagination_last(client, common_query_builder):
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
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {
                'last': num,
            }
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['neoantigens']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_cursor_distinct_pagination(client, common_query):
    page_num = 2
    num = 10
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'paging': {
                'page': page_num,
                'first': num,
            },
            'distinct': True,
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['neoantigens']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_query(client, common_query):
    response = client.post(
        '/api', json={
            'query': common_query,
            'variables': {'paging': {'first': 3, }}
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['neoantigens']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:3]:
        assert type(result['id']) is str
        assert type(result['tpm']) is float or NoneType
        assert type(result['pmhc']) is str
        assert type(result['freqPmhc']) is int
        assert type(result['gene']['entrez']) is int or NoneType
        assert type(result['gene']['hgnc']) is int or NoneType
        assert type(result['patient']['barcode']) is str


def test_query_specific_neoantigen(client, common_query, test_neoantigen, test_gene, test_patient):
    response = client.post(
        '/api', json={
            'query': common_query,
            'variables': {
                'pmhc': [test_neoantigen.pmhc],
                'entrez': [test_gene.entrez_id],
                'patient': [test_patient.name]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['neoantigens']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:3]:
        assert type(result['id']) is str
        assert result['tpm'] == test_neoantigen.tpm
        assert result['pmhc'] == test_neoantigen.pmhc
        assert result['freqPmhc'] == test_neoantigen.freq_pmhc
        assert result['gene']['entrez'] == test_gene.entrez_id
        assert result['gene']['hgnc'] == test_gene.hgnc_id
        assert result['patient']['barcode'] == test_patient.name
