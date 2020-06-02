import pytest
from tests import app
from flaskr.db_models import Sample


def test_sample_type(app):
    app()
    sample_type_name = 'DO1328'
    result = Sample.query.filter_by(name=sample_type_name).first()

    assert result.name == sample_type_name
    assert repr(result) == '<Sample %r>' % sample_type_name
