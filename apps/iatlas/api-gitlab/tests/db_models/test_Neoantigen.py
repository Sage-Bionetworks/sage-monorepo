import pytest
from tests import NoneType
from api.database import return_neoantigen_query
from api.db_models import Neoantigen


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
    return query.limit(1).one_or_none()


def test_Neoantigen_no_relations(app):
    query = return_neoantigen_query()
    results = query.limit(10).all()
    assert isinstance(results, list)
    assert len(results) == 10
    for result in results:
        assert type(result.id) is str
        assert type(result.tpm) is float or NoneType
        assert type(result.pmhc) is str
        assert type(result.freq_pmhc) is int
        assert type(result.patient_id) is str
        assert type(result.neoantigen_gene_id) is str or NoneType
        assert result.patient == []
        assert result.gene == []
        string_representation = '<Neoantigen %r>' % result.id
        assert repr(result) == string_representation


def test_Neoantigen_with_relations(app):
    query = return_neoantigen_query("patient", "gene")
    results = query.limit(10).all()
    assert isinstance(results, list)
    assert len(results) == 10
    for result in results:
        assert type(result.id) is str
        assert type(result.tpm) is float or NoneType
        assert type(result.pmhc) is str
        assert type(result.freq_pmhc) is int
        assert type(result.patient_id) is str
        assert type(result.neoantigen_gene_id) is str or NoneType
        assert result.patient[0].id == result.patient_id
        assert result.gene[0].id == result.neoantigen_gene_id
        string_representation = '<Neoantigen %r>' % result.id
        assert repr(result) == string_representation


def test_specific_Neoantigen(app, test_neoantigen):
    query = return_neoantigen_query()
    query = query.filter_by(patient_id=test_neoantigen.patient_id)
    query = query.filter_by(
        neoantigen_gene_id=test_neoantigen.neoantigen_gene_id)
    query = query.filter_by(pmhc=test_neoantigen.pmhc)
    result = query.one_or_none()
    assert result.id == test_neoantigen.id
    #assert result.tpm == test_neoantigen.tpm
    assert result.pmhc == test_neoantigen.pmhc
    assert result.freq_pmhc == test_neoantigen.freq_pmhc
    assert result.patient_id == test_neoantigen.patient_id
    assert result.neoantigen_gene_id == test_neoantigen.neoantigen_gene_id
    assert result.patient == []
    assert result.gene == []
