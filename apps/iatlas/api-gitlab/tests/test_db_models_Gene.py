import json
import os
import pytest
from tests import app, client
from flaskr.db_models import Gene


def test_Gene(app):
    app()
    entrez = 135
    hgnc = 'ADORA2A'
    description = 'It is a popular target in immuno-oncology due to its function of immunosuppressive effects in tumor microenvironment.'
    io_landscape_name = hgnc
    references = [
        'https://www.cancerresearch.org/scientists/immuno-oncology-landscape?2019IOpipelineDB=2017;Target;ADORA2A']
    node_type_id = 2
    pathway_id = 3
    therapy_type_id = 2

    result = Gene.query.filter_by(entrez=entrez).first()

    assert result.entrez == entrez
    assert result.hgnc == hgnc
    assert result.description == description
    assert result.io_landscape_name == io_landscape_name
    assert result.references == references
    assert result.node_type_id == node_type_id
    assert result.pathway_id == pathway_id
    assert result.therapy_type_id == therapy_type_id
    assert repr(result) == '<Gene %r>' % entrez
