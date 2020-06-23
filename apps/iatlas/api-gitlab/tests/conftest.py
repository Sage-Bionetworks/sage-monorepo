import pytest
from flaskr import create_app
from tests import db, TestConfig


@pytest.fixture(scope='function')
def app():
    app = create_app(TestConfig)
    app.test_request_context().push()

    yield app
    db.session.remove()


@pytest.fixture(scope='function')
def client(app):
    app = create_app(TestConfig)
    with app.test_client() as client:
        yield client


@pytest.fixture(scope='function')
def test_db(app):
    from flaskr import db
    yield db


@pytest.fixture(scope='session')
def dataset():
    return 'TCGA'


@pytest.fixture(scope='session')
def dataset_id():
    return 5


@pytest.fixture(scope='session')
def related():
    return 'Immune_Subtype'


@pytest.fixture(scope='session')
def chosen_feature():
    return 'Det_Ratio'


@pytest.fixture(scope='session')
def feature_class():
    return 'TIL Map Characteristic'


@pytest.fixture(scope='session')
def entrez():
    return 3627


@pytest.fixture(scope='session')
def hgnc():
    return 'CXCL10'
