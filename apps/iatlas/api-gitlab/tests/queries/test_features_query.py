import json
import pytest
from tests import NoneType
from api.enums import unit_enum
from api.database import return_feature_query
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging
import logging


@pytest.fixture(scope='module')
def feature_name():
    return 'Eosinophils'


@pytest.fixture(scope='module')
def germline_category():
    return 'Leukocyte Subset ES'


@pytest.fixture(scope='module')
def germline_module():
    return 'Cytotoxic'


@pytest.fixture(scope='module')
def max_value():
    return 5.7561021


@pytest.fixture(scope='module')
def min_value():
    return 0.094192693


@pytest.fixture(scope='module')
def cohort_name():
    return 'tcga_immune_subtype'


@pytest.fixture(scope='module')
def cohort_id(test_db, cohort_name):
    from api.db_models import Cohort
    (id, ) = test_db.session.query(Cohort.id).filter_by(
        name=cohort_name).one_or_none()
    return id


@pytest.fixture(scope='module')
def cohort_query_builder():
    def f(query_fields):
        return """
        query Cohorts(
            $paging: PagingInput
            $distinct:Boolean
            $name: [String!]
            $dataSet: [String!]
            $tag: [String!]
            $clinical: [String!]
        ) {
        cohorts(
            paging: $paging
            distinct: $distinct
            name: $name
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
def cohort_samples(client, cohort_name, cohort_query):
    response = client.post('/api', json={'query': cohort_query, 'variables': {
        'name': [cohort_name]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    cohort = page['items'][0]
    samples = cohort['samples']
    names = [sample['name'] for sample in samples]
    return names


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """
        query Features($feature: [String!]
            $featureClass: [String!]
            $cohort: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
            $paging: PagingInput
            $distinct: Boolean
        ) {
        features(
            feature: $feature
            featureClass: $featureClass
            cohort: $cohort
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
            paging: $paging
            distinct: $distinct
            )
        """ + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder(
        """
        {
            items {
                class
                display
                name
                order
                unit
                methodTag
                germline_module
                germline_category
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
        }
        """
    )


@pytest.fixture(scope='module')
def samples_query(common_query_builder):
    return common_query_builder(
        """
        {
            items {
                id
                name
                class
                samples {
                    name
                    value
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
        }
        """
    )


@pytest.fixture(scope='module')
def values_query(common_query_builder):
    return common_query_builder(
        """
        {
            items {
                name
                class
                valueMin
                valueMax
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
        }
        """
    )


def test_features_cursor_pagination_first(client, common_query_builder):
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
    num = 5
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {'first': num}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    items = page['items']
    paging = page['paging']
    #logger = logging.getLogger("test_feature")
    # logger.info(page)
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == True
    assert paging['hasPreviousPage'] == False
    assert start == items[0]['id']
    assert end == items[num - 1]['id']
    assert int(end) - int(start) > 0


def test_features_cursor_pagination_last(client, common_query_builder):
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
    num = 5
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {
                'last': num,
                'before': to_cursor_hash(1000)
            }
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_features_cursor_distinct_pagination(client, common_query):
    page_num = 2
    num = 2
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'paging': {
                'page': page_num,
                'first': num,
            },
            'distinct': True,
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_features_query_with_no_args(client, common_query):
    response = client.post('/api', json={'query': common_query})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    # Get the total number of features in the database.
    feature_count = return_feature_query('id').count()

    assert isinstance(features, list)
    assert len(features) == feature_count
    for feature in features[0:3]:
        assert type(feature['name']) is str
        assert type(feature['display']) is str
        assert type(feature['class']) is str
        assert type(feature['methodTag']) is str or NoneType
        assert type(feature['order']) is int or NoneType
        assert feature['unit'] in unit_enum.enums or type(
            feature['unit']) is NoneType
        assert type(feature['germline_module']) is str or NoneType
        assert type(feature['germline_category']) is str or NoneType


def test_features_query_with_feature(client, chosen_feature, common_query):
    response = client.post(
        '/api', json={
            'query': common_query,
            'variables': {'feature': [chosen_feature]}
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) == 1
    feature = features[0]
    assert feature['name'] == chosen_feature
    assert type(feature['display']) is str
    assert type(feature['class']) is str
    assert type(feature['methodTag']) is str or NoneType
    assert type(feature['order']) is int or NoneType
    assert feature['unit'] in unit_enum.enums or type(
        feature['unit']) is NoneType
    assert type(feature['germline_module']) is str or NoneType
    assert type(feature['germline_category']) is str or NoneType


def test_features_query_with_feature_class(client, feature_class, common_query):
    response = client.post(
        '/api', json={
            'query': common_query,
            'variables': {'featureClass': [feature_class]}
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) > 1
    for feature in features:
        assert type(feature['name']) is str
        assert type(feature['display']) is str
        assert feature['class'] == feature_class
        assert type(feature['methodTag']) is str or NoneType
        assert type(feature['order']) is int or NoneType
        assert feature['unit'] in unit_enum.enums or type(
            feature['unit']) is NoneType
        assert type(feature['germline_module']) is str or NoneType
        assert type(feature['germline_category']) is str or NoneType


def test_features_query_with_feature_and_feature_class(client, chosen_feature, feature_class, common_query):
    response = client.post(
        '/api', json={
            'query': common_query,
            'variables': {
                'feature': [chosen_feature],
                'featureClass': [feature_class]
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) == 1
    feature = features[0]
    assert feature['name'] == chosen_feature
    assert type(feature['display']) is str
    assert feature['class'] == feature_class
    assert type(feature['methodTag']) is str or NoneType
    assert type(feature['order']) is int or NoneType
    assert feature['unit'] in unit_enum.enums or type(
        feature['unit']) is NoneType
    assert type(feature['germline_module']) is str or NoneType
    assert type(feature['germline_category']) is str or NoneType

# todo: should results be distinct when passing max value and not requesting samples?


def test_features_query_with_passed_max_value(client, chosen_feature, max_value, values_query):
    response = client.post(
        '/api', json={'query': values_query,
                      'variables': {
                          'feature': [chosen_feature],
                          'maxValue': max_value
                      }})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    #logger = logging.getLogger("test_feature")
    # logger.info(features)
    assert len(features) == 1
    feature = features[0]
    assert feature['name'] == chosen_feature
    assert feature['valueMax'] <= max_value

# todo: should results be distinct when passing min value and not requesting samples?


def test_features_query_with_passed_min_value(client, chosen_feature, min_value, values_query):
    response = client.post(
        '/api', json={'query': values_query,
                      'variables': {
                          'feature': [chosen_feature],
                          'minValue': min_value
                      }})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) == 1
    feature = features[0]
    assert feature['name'] == chosen_feature
    assert feature['valueMax'] >= min_value


'''
def test_features_query_with_passed_max_value_and_class(client, feature_class, max_value, values_query):
    response = client.post(
        '/api', json={'query': values_query,
                      'variables': {
                          'featureClass': [feature_class],
                          'maxValue': max_value
                      }})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) > 1
    for feature in features:
        assert feature['class'] == feature_class
        assert feature['valueMax'] <= max_value


def test_features_query_with_passed_min_value_and_class(client, feature_class, min_value, values_query):
    response = client.post(
        '/api', json={'query': values_query,
                      'variables': {
                          'featureClass': [feature_class],
                          'minValue': min_value
                      }})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) > 1
    for feature in features:
        assert feature['class'] == feature_class
        assert feature['valueMax'] >= min_value
'''


def test_feature_samples_query_with_feature(client, feature_name, samples_query):
    response = client.post(
        '/api', json={
            'query': samples_query,
            'variables': {'feature': [feature_name]}
        })
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) == 1
    feature = features[0]
    samples = feature['samples']
    assert feature['name'] == feature_name
    assert type(feature['class']) is str
    assert isinstance(samples, list)
    assert len(samples) > 0
    for sample in samples[0:2]:
        assert type(sample['name']) is str
        assert type(sample['value']) is float


def test_feature_samples_query_with_class(client, feature_class, samples_query):
    response = client.post(
        '/api', json={
            'query': samples_query,
            # 'variables': {'featureClass': [feature_class]}
            'variables': {'featureClass': ['EPIC']}
        })
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    # assert len(features) == 10
    for feature in features:
        samples = feature['samples']
        assert feature['class'] == 'EPIC'
        # assert feature['class'] == feature_class
        assert type(feature['name']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples[0:2]:
            assert type(sample['name']) is str
            assert type(sample['value']) is float


def test_feature_samples_query_with_feature_and_cohort(client, feature_name, samples_query, cohort_name, cohort_samples):
    response = client.post(
        '/api', json={
            'query': samples_query,
            'variables': {
                'feature': [feature_name],
                'cohort': [cohort_name]
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']
    #logger = logging.getLogger("test_feature")
    # logger.info(cohort_samples[0:10])
    # logger.info(features[0])
    assert isinstance(features, list)
    assert len(features) == 1
    feature = features[0]
    samples = feature['samples']
    assert feature['name'] == feature_name
    assert type(feature['class']) is str
    assert isinstance(samples, list)
    assert len(samples) > 0
    for sample in samples[0:2]:
        assert type(sample['name']) is str
        assert type(sample['value']) is float
        assert sample['name'] in cohort_samples
    assert False


def test_pcawg_feature_samples_query_with_feature_and_cohort(client, feature_name, samples_query, cohort_name, cohort_samples):
    response = client.post(
        '/api', json={
            'query': samples_query,
            'variables': {
                'feature': [feature_name],
                'cohort': ['pcawg_gender']
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']
    logger = logging.getLogger("test_feature")
    # logger.info(cohort_samples[0:10])
    # logger.info(features[5])
    assert isinstance(features, list)
    for feature in features:
        logger.info(feature['name'])
        logger.info(feature['samples'][1000])
    assert len(features) == 1
    feature = features[0]
    samples = feature['samples']
    assert feature['name'] == feature_name
    assert type(feature['class']) is str
    assert isinstance(samples, list)
    assert len(samples) > 0
    for sample in samples[0:2]:
        assert type(sample['name']) is str
        assert type(sample['value']) is float
        assert sample['name'] in cohort_samples
    assert False


'''
def test_features_query_with_passed_sample(client, common_query, data_set, related, sample):
    response = client.post(
        '/api', json={'query': common_query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'sample': [sample]}})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) > 0
    # Don't need to iterate through every result.
    for feature in features[0:2]:
        assert type(feature['display']) is str or NoneType
        assert type(feature['name']) is str
        assert type(feature['order']) is int or NoneType
        assert feature['unit'] in unit_enum.enums or type(
            feature['unit']) is NoneType


def test_features_query_max_value(client, data_set, related, chosen_feature, values_query):
    response = client.post(
        '/api', json={'query': values_query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) > 0
    # Don't need to iterate through every result.
    for feature in features[0:2]:
        assert feature['name'] == chosen_feature
        assert type(feature['valueMax']) is float


def test_features_query_min_value(client, data_set, related, chosen_feature, values_query):
    response = client.post(
        '/api', json={'query': values_query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) > 0
    # Don't need to iterate through every result.
    for feature in features[0:2]:
        assert feature['name'] == chosen_feature
        assert type(feature['valueMin']) is float


def test_features_query_no_relations(client, common_query, data_set, related, chosen_feature):
    response = client.post(
        '/api', json={'query': common_query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) == 1
    for feature in features:
        assert type(feature['display']) is str or NoneType
        assert 'methodTag' not in feature
        assert feature['name'] == chosen_feature
        assert type(feature['order']) is int or NoneType
        assert type(
            feature['unit']) is NoneType or feature['unit'] in unit_enum.enums
'''
