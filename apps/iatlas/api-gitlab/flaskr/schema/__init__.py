from ariadne import load_schema_from_path, make_executable_schema, ObjectType
import os
from flaskr.resolvers import (
    resolve_gene, resolve_genes, resolve_tags, resolve_test)

dirname, _filename = os.path.split(os.path.abspath(__file__))


root_query = load_schema_from_path(dirname + "/root.query.graphql")
gene_query = load_schema_from_path(dirname + "/gene.query.graphql")
tag_query = load_schema_from_path(dirname + "/tag.query.graphql")

type_defs = [root_query, tag_query, gene_query]


root = ObjectType("Query")
gene = ObjectType("Gene")
tag = ObjectType("Tag")

root.set_field('gene', resolve_gene)
root.set_field('genes', resolve_genes)
root.set_field('tags', resolve_tags)
root.set_field('test', resolve_test)


schema = make_executable_schema(
    type_defs, [root, gene, tag])
