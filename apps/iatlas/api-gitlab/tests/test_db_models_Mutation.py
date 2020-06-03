import pytest
from tests import app, NoneType
from flaskr.db_models import Mutation


def test_Mutation(app):
    app()
    gene_id = 77
    string_representation_list = []
    separator = ', '

    results = Mutation.query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        mutation_id = result.id
        string_represntation = '<Mutation %r>' % mutation_id
        string_representation_list.append(string_represntation)
        assert result.gene_id == gene_id
        assert type(result.gene_id) is int
        assert type(result.mutation_code_id) is int
        assert type(result.mutation_type_id) is int or NoneType
        assert repr(result) == string_represntation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
