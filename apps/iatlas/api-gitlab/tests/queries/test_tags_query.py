import json
import pytest
from api.database import return_tag_query
from tests import NoneType


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Tags(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
        ) {
            tags(
                dataSet: $dataSet
                related: $related
                tag: $tag
                feature: $feature
                featureClass: $featureClass
                sample: $sample
        )""" + query_fields + "}"
    return f


def test_tags_query_with_data_set_related_and_feature(client, common_query_builder, data_set, related, chosen_feature):
    query = common_query_builder("""{
                                    characteristics
                                    color
                                    longDisplay
                                    name
                                    shortDisplay
                                    related {
                                        name
                                        characteristics
                                        color
                                        longDisplay
                                        shortDisplay
                                    }
                                    sampleCount
                                    samples
                                }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    results = json_data['data']['tags']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        related = result['related']
        samples = result['samples']
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['longDisplay']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['sampleCount']) is int
        assert isinstance(related, list)
        assert len(related) > 0
        for current_related in related[0:2]:
            assert type(current_related["name"]) is str
            assert type(current_related["characteristics"]) is str or NoneType
            assert type(current_related["color"]) is str or NoneType
            assert type(current_related["longDisplay"]) is str or NoneType
            assert type(current_related["shortDisplay"]) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert type(current_sample) is str


def test_tags_query_no_data_set_and_related(client, common_query_builder, data_set, related):
    query = common_query_builder("""{
                                    characteristics
                                    color
                                    shortDisplay
                                    name
                                }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related]}})
    json_data = json.loads(response.data)
    results = json_data['data']['tags']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str
        assert not 'sampleCount' in result
        assert not 'samples' in result


def test_tags_query_with_data_set_related_and_feature_class(client, common_query_builder, data_set, related, feature_class):
    query = common_query_builder("""{
                                    characteristics
                                    color
                                    shortDisplay
                                    name
                                }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'featureClass': [feature_class]}})
    json_data = json.loads(response.data)
    results = json_data['data']['tags']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert type(result['name']) is str


def test_tags_query_with_data_set_related_and_tag(client, common_query_builder, data_set, related, tag):
    query = common_query_builder("""{
                                    name
                                    sampleCount
                                }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'tag': [tag]}})
    json_data = json.loads(response.data)
    results = json_data['data']['tags']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['name'] == tag
        assert type(result['sampleCount']) is int


def test_tags_query_with_data_set_related_tag_and_sample(client, common_query_builder, data_set, related, tag, sample):
    query = common_query_builder("""{
                                    name
                                    sampleCount
                                    samples
                                }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'tag': [tag],
                                    'sample': [sample]}})
    json_data = json.loads(response.data)
    results = json_data['data']['tags']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        samples = result['samples']
        assert result['name'] == tag
        assert result['sampleCount'] == 1
        assert isinstance(samples, list)
        assert len(samples) == 1
        for current_sample in samples:
            assert current_sample == sample


def test_tags_query_with_no_args(client, common_query_builder):
    query = common_query_builder("""{
                                    name
                                    sampleCount
                                }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['tags']

    # Get the total number of tags in the database.
    tag_count = return_tag_query('id').count()

    assert isinstance(results, list)
    assert len(results) == tag_count
    for result in results:
        assert type(result['name']) is str
        assert type(result['sampleCount']) is int
