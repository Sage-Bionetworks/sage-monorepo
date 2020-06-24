import pytest
from api.database import return_feature_class_query


@pytest.fixture(scope='module')
def name():
    return 'Adaptive Receptor - B cell'


def test_FeatureClass_with_relations(app, name):
    relationships_to_join = ['features']

    query = return_feature_class_query(*relationships_to_join)
    result = query.filter_by(name=name).first()

    assert isinstance(result.features, list)
    # Don't need to iterate through every result.
    for feature in result.features[0:2]:
        assert type(feature.name) is str
    assert type(result.id) is int
    assert result.name == name
    assert repr(result) == '<FeatureClass %r>' % name


def test_FeatureClass_no_relations(app, name):
    query = return_feature_class_query()
    result = query.filter_by(name=name).first()

    assert result.features == []
    assert type(result.id) is int
    assert result.name == name
