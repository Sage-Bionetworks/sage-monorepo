from ariadne import load_schema_from_path, make_executable_schema, ObjectType
import os
from flaskr.resolvers import (
    resolve_dataSet, resolve_gene, resolve_genes, resolve_test, resolve_mutation)

dirname, _filename = os.path.split(os.path.abspath(__file__))


data_set_query = load_schema_from_path(dirname + "/dataSet.query.graphql")
gene_query = load_schema_from_path(dirname + "/gene.query.graphql")
root_query = load_schema_from_path(dirname + "/root.query.graphql")
mutation_query = load_schema_from_path(dirname + "/mutation.query.graphql")

type_defs = [root_query, data_set_query, gene_query, mutation_query]


root = ObjectType("Query")
data_set = ObjectType("DataSet")
gene = ObjectType("Gene")
mutation = ObjectType("Mutation")

root.set_field('dataSet', resolve_dataSet)
root.set_field('gene', resolve_gene)
root.set_field('genes', resolve_genes)
root.set_field('test', resolve_test)
root.set_field('mutation', resolve_mutation)


schema = make_executable_schema(
    type_defs, [root, data_set, gene, mutation])
