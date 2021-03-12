import pytest
from tests import NoneType
from api.database import return_snp_query


@pytest.fixture(scope='module')
def snp_name():
    return '7:104003135:C:G'


@pytest.fixture(scope='module')
def snp_rsid():
    return 'rs2188491'


@pytest.fixture(scope='module')
def snp_chr():
    return '7'


def test_snp(app, snp_name, snp_rsid, snp_chr):
    query = return_snp_query('snps')
    results = query.filter_by(name=snp_name).filter_by(
        rsid=snp_rsid).filter_by(chr=snp_chr).limit(3).all()
    assert isinstance(results, list)
    assert len(results) > 0
    string_representation_list = []
    separator = ', '
    for result in results:
        string_representation = '<Snp %r>' % result.name
        string_representation_list.append(string_representation)
        assert type(result.name) is str
        assert type(result.rsid) is str or NoneType
        assert type(result.chr) is str or NoneType
        assert type(result.bp) is int or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
