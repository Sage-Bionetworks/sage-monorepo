import json
import pytest
from tests import NoneType
from api.enums import unit_enum
from api.database import return_feature_query
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging


@pytest.fixture(scope='module')
def feature_name():
    return 'Eosinophils'


@pytest.fixture(scope='module')
def max_value():
    return 5.7561021


@pytest.fixture(scope='module')
def min_value():
    return 0.094192693


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
                germlineModule
                germlineCategory
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
                order
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
        assert type(feature['germlineModule']) is str or NoneType
        assert type(feature['germlineCategory']) is str or NoneType


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
    assert type(feature['germlineModule']) is str or NoneType
    assert type(feature['germlineCategory']) is str or NoneType


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
        assert type(feature['germlineModule']) is str or NoneType
        assert type(feature['germlineCategory']) is str or NoneType


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
    assert type(feature['germlineModule']) is str or NoneType
    assert type(feature['germlineCategory']) is str or NoneType


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
    assert len(features) == 1
    feature = features[0]
    assert feature['name'] == chosen_feature
    assert feature['valueMax'] <= max_value


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


def test_features_query_values(client, feature_name, min_value, values_query):
    response = client.post(
        '/api', json={'query': values_query,
                      'variables': {
                          'feature': [feature_name],
                          'minValue': min_value
                      }})
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) == 1
    for feature in features:
        assert feature['name'] == feature_name
        assert feature['valueMax'] >= feature['valueMin']


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
            'variables': {'featureClass': [feature_class]}
        })
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']

    assert isinstance(features, list)
    assert len(features) == 10
    for feature in features:
        samples = feature['samples']
        assert feature['class'] == feature_class
        assert type(feature['name']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples[0:2]:
            assert type(sample['name']) is str
            assert type(sample['value']) is float


def test_feature_samples_query_with_feature_and_cohort(client, feature_name, samples_query, tcga_tag_cohort_name, tcga_tag_cohort_samples):
    response = client.post(
        '/api', json={
            'query': samples_query,
            'variables': {
                'feature': [feature_name],
                'cohort': [tcga_tag_cohort_name]
            }
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
        assert sample['name'] in tcga_tag_cohort_samples


def test_pcawg_feature_samples_query_with_feature_and_cohort(client, feature_name, samples_query, pcawg_cohort_name, pcawg_cohort_samples):
    response = client.post(
        '/api', json={
            'query': samples_query,
            'variables': {
                'feature': [feature_name],
                'cohort': [pcawg_cohort_name]
            }
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
    for sample in samples:
        assert type(sample['name']) is str
        assert type(sample['value']) is float
        assert sample['name'] in pcawg_cohort_samples


def test_feature_samples_query_with_feature_and_sample(client, feature_name, samples_query, sample):
    response = client.post(
        '/api', json={
            'query': samples_query,
            'variables': {
                'feature': [feature_name],
                'sample': [sample]
            }
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
    assert len(samples) == 1
    for s in samples:
        assert s['name'] == sample
        assert type(s['value']) is float


def test_features_query_with_germline_feature(client, common_query, germline_feature, germline_module, germline_category):
    response = client.post(
        '/api', json={
            'query': common_query,
            'variables': {
                'feature': [germline_feature]
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']
    assert isinstance(features, list)
    assert len(features) == 1
    feature = features[0]
    assert feature['name'] == germline_feature
    assert feature['germlineModule'] == germline_module
    assert feature['germlineCategory'] == germline_category


def test_feature_samples_query_with_class_and_cohort(client, samples_query, feature_class2, feature_class2_feature_names, tcga_tag_cohort_name, tcga_tag_cohort_samples):
    response = client.post(
        '/api', json={
            'query': samples_query,
            'variables': {
                'featureClass': [feature_class2],
                'cohort': [tcga_tag_cohort_name]
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']
    assert isinstance(features, list)
    assert len(features) == 10
    feature = features[0]
    samples = feature['samples']
    assert feature['name'] in feature_class2_feature_names
    assert feature['class'] == feature_class2
    assert isinstance(samples, list)
    assert len(samples) > 0
    for sample in samples[0:2]:
        assert type(sample['name']) is str
        assert type(sample['value']) is float
        assert sample['name'] in tcga_tag_cohort_samples


def test_feature_samples_query_with_class_and_pcawg_cohort(client, samples_query, feature_class2, pcawg_cohort_name):
    response = client.post(
        '/api', json={
            'query': samples_query,
            'variables': {
                'featureClass': [feature_class2],
                'cohort': [pcawg_cohort_name]
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['features']
    features = page['items']
    assert isinstance(features, list)
    assert len(features) == 0
