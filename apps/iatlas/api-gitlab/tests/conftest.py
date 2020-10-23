import pytest
from api import create_app
from tests import db, TestConfig


@pytest.fixture(scope='session')
def app():
    app = create_app(TestConfig)
    app.test_request_context().push()

    yield app
    db.session.remove()


@pytest.fixture(scope='session')
def client():
    app = create_app(TestConfig)
    with app.test_client() as client:
        yield client


@pytest.fixture(scope='session')
def test_db(app):
    from api import db
    yield db


@pytest.fixture(scope='session')
def data_set():
    return 'TCGA'


@pytest.fixture(scope='session')
def data_set_id(test_db, data_set):
    from api.db_models import Dataset
    (id, ) = test_db.session.query(Dataset.id).filter_by(
        name=data_set).one_or_none()
    return id


@ pytest.fixture(scope='session')
def related():
    return 'Immune_Subtype'


@ pytest.fixture(scope='session')
def tag():
    return 'C1'


@ pytest.fixture(scope='session')
def chosen_feature():
    return 'Det_Ratio'


@ pytest.fixture(scope='session')
def feature_class():
    return 'TIL Map Characteristic'


@ pytest.fixture(scope='session')
def entrez():
    return 3627


@pytest.fixture(scope='session')
def gene_id(test_db, entrez):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(entrez=entrez).one_or_none()
    return id


@ pytest.fixture(scope='session')
def hgnc():
    return 'CXCL10'


@ pytest.fixture(scope='session')
def mutation_type():
    return 'driver_mutation'


# Sample id 617
@ pytest.fixture(scope='session')
def sample():
    return 'TCGA-05-4420'


@ pytest.fixture(scope='session')
def sample_id(test_db, sample):
    from api.db_models import Sample
    (id, ) = test_db.session.query(Sample.id).filter_by(name=sample).one_or_none()
    return id


@ pytest.fixture(scope='session')
def slide():
    return 'TCGA-05-4244-01Z-00-DX1'


# Patient id 617
@ pytest.fixture(scope='session')
def patient():
    return 'TCGA-05-4420'


@ pytest.fixture(scope='session')
def max_age_at_diagnosis():
    return 86


@ pytest.fixture(scope='session')
def min_age_at_diagnosis():
    return 18


@ pytest.fixture(scope='session')
def ethnicity():
    return 'not hispanic or latino'


@ pytest.fixture(scope='session')
def gender():
    return 'female'


@ pytest.fixture(scope='session')
def max_height():
    return 179


@ pytest.fixture(scope='session')
def min_height():
    return 130


@ pytest.fixture(scope='session')
def race():
    return 'black or african american'


@ pytest.fixture(scope='session')
def max_weight():
    return 160


@ pytest.fixture(scope='session')
def min_weight():
    return 42
