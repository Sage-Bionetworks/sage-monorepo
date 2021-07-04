import pytest
from api import create_app, db
import json


@pytest.fixture(autouse=True)
def enable_transactional_tests(db_session):
    """
    Automatically enable transactions for all tests, without importing any extra fixtures.
    """
    pass


@pytest.fixture(scope='session')
def app():
    app = create_app(test=True)
    app.test_request_context().push()

    yield app
    db.session.remove()


@pytest.fixture(scope='session')
def client(app):
    with app.test_client() as client:
        yield client


@pytest.fixture(scope='session')
def test_db(app):
    from api import db
    yield db


@pytest.fixture(scope='session')
def _db(test_db):
    yield test_db
    test_db.session.remove()


@pytest.fixture(scope='session')
def data_set():
    return 'TCGA'


@pytest.fixture(scope='session')
def data_set_id(test_db, data_set):
    from api.db_models import Dataset
    (id, ) = test_db.session.query(Dataset.id).filter_by(
        name=data_set).one_or_none()
    return id


@pytest.fixture(scope='session')
def pcawg_data_set():
    return 'PCAWG'


@pytest.fixture(scope='session')
def pcawg_data_set_id(test_db, pcawg_data_set):
    from api.db_models import Dataset
    (id, ) = test_db.session.query(Dataset.id).filter_by(
        name=pcawg_data_set).one_or_none()
    return id


@ pytest.fixture(scope='session')
def related():
    return 'Immune_Subtype'


@ pytest.fixture(scope='session')
def related_id(test_db, related):
    from api.db_models import Tag
    (id, ) = test_db.session.query(Tag.id).filter_by(
        name=related).one_or_none()
    return id


@ pytest.fixture(scope='session')
def tag():
    return 'C1'


@ pytest.fixture(scope='session')
def tag_id(test_db, tag):
    from api.db_models import Tag
    (id, ) = test_db.session.query(Tag.id).filter_by(
        name=tag).one_or_none()
    return id


@ pytest.fixture(scope='session')
def chosen_feature():
    return 'Det_Ratio'


@ pytest.fixture(scope='session')
def chosen_feature_id(test_db, chosen_feature):
    from api.db_models import Feature
    (id, ) = test_db.session.query(Feature.id).filter_by(
        name=chosen_feature).one_or_none()
    return id


@ pytest.fixture(scope='session')
def feature_class():
    return 'TIL Map Characteristic'


@ pytest.fixture(scope='session')
def feature_class2():
    return 'DNA Alteration'


@ pytest.fixture(scope='session')
def feature_class2_id(test_db, feature_class2):
    from api.db_models import FeatureClass
    (id, ) = test_db.session.query(FeatureClass.id).filter_by(
        name=feature_class2).one_or_none()
    return id


@ pytest.fixture(scope='session')
def feature_class2_feature_names(test_db, feature_class2_id):
    from api.db_models import Feature
    res = test_db.session.query(Feature.name).filter_by(
        class_id=feature_class2_id).all()
    features = [feature[0] for feature in res]
    return features


@ pytest.fixture(scope='session')
def feature3():
    return 'height'


@ pytest.fixture(scope='session')
def feature3_class():
    return 'Clinical'


@ pytest.fixture(scope='session')
def feature3_id(test_db, feature3):
    from api.db_models import Feature
    (id, ) = test_db.session.query(Feature.id).filter_by(
        name=feature3).one_or_none()
    return id


@ pytest.fixture(scope='session')
def entrez():
    return 3627


@pytest.fixture(scope='session')
def gene_id(test_db, entrez):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(entrez=entrez).one_or_none()
    return id


@ pytest.fixture(scope='session')
def hgnc(test_db, entrez):
    from api.db_models import Gene
    (hgnc, ) = test_db.session.query(
        Gene.hgnc).filter_by(entrez=entrez).one_or_none()
    return hgnc


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

# ----


@pytest.fixture(scope='module')
def cohort_query_builder():
    def f(query_fields):
        return """
        query Cohorts(
            $paging: PagingInput
            $distinct:Boolean
            $cohort: [String!]
            $dataSet: [String!]
            $tag: [String!]
            $clinical: [String!]
        ) {
        cohorts(
            paging: $paging
            distinct: $distinct
            cohort: $cohort
            dataSet: $dataSet
            tag: $tag
            clinical: $clinical
        )
        """ + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def cohort_query(cohort_query_builder):
    return cohort_query_builder(
        """
        {
            items {
                name
                samples {
                    name
                }
            }
        }
        """
    )


@pytest.fixture(scope='module')
def tcga_tag_cohort_name():
    return 'TCGA_Immune_Subtype'


@pytest.fixture(scope='module')
def pcawg_clinical_cohort_name():
    return('PCAWG_Gender')


@pytest.fixture(scope='module')
def tcga_tag_cohort_id(test_db, tcga_tag_cohort_name):
    from api.db_models import Cohort
    (id, ) = test_db.session.query(Cohort.id).filter_by(
        name=tcga_tag_cohort_name).one_or_none()
    return id


@pytest.fixture(scope='module')
def pcawg_clinical_cohort_id(test_db, pcawg_clinical_cohort_name):
    from api.db_models import Cohort
    (id, ) = test_db.session.query(Cohort.id).filter_by(
        name=pcawg_clinical_cohort_name).one_or_none()
    return id


@pytest.fixture(scope='module')
def tcga_tag_cohort_samples(client, tcga_tag_cohort_name, cohort_query):
    response = client.post('/api', json={'query': cohort_query, 'variables': {
        'cohort': [tcga_tag_cohort_name]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    cohort = page['items'][0]
    samples = cohort['samples']
    names = [sample['name'] for sample in samples]
    return names


@pytest.fixture(scope='module')
def pcawg_clinical_cohort_samples(client, pcawg_clinical_cohort_name, cohort_query):
    response = client.post('/api', json={'query': cohort_query, 'variables': {
        'cohort': [pcawg_clinical_cohort_name]
    }})
    import logging
    logger = logging.getLogger("feature response")
    logger.info(pcawg_clinical_cohort_name)
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    cohort = page['items'][0]
    samples = cohort['samples']
    names = [sample['name'] for sample in samples]
    return names

# for testing germline fields ----


@pytest.fixture(scope='module')
def germline_feature():
    return 'BCR_Richness'


@pytest.fixture(scope='module')
def germline_pathway():
    return 'MMR'


@pytest.fixture(scope='module')
def germline_category():
    return 'Adaptive Receptor'


@pytest.fixture(scope='module')
def germline_module():
    return 'Unassigned'
