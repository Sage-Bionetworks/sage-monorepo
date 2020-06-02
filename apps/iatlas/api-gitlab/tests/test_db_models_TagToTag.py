import json
import os
import pytest
from tests import app, NoneType
from flaskr.db_models import TagToTag


def test_TagToTag(app):
    app()
    tag_id = 64

    results = TagToTag.query.filter_by(tag_id=tag_id).all()

    assert isinstance(results, list)
    for result in results:
        assert result.tag_id == tag_id
        assert type(result.related_tag_id) is int
        assert repr(result) == '<TagToTag %r>' % tag_id
    assert repr(results) == '[<TagToTag %r>]' % tag_id
