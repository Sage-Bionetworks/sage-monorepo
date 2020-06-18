from ariadne import load_schema_from_path, make_executable_schema, ObjectType
import os
from flaskr.resolvers import (
    resolve_gene, resolve_genes, resolve_tags, resolve_test, resolve_mutation, resolve_mutations, resolve_patient)

dirname, _filename = os.path.split(os.path.abspath(__file__))


root_query = load_schema_from_path(dirname + "/root.query.graphql")
gene_query = load_schema_from_path(dirname + "/gene.query.graphql")
tag_query = load_schema_from_path(dirname + "/tag.query.graphql")
mutation_query = load_schema_from_path(dirname + "/mutation.query.graphql")
patient_query = load_schema_from_path(dirname + "/patient.query.graphql")

type_defs = [root_query, tag_query, gene_query, mutation_query, patient_query]


root = ObjectType("Query")
gene = ObjectType("Gene")
tag = ObjectType("Tag")
mutation = ObjectType("Mutation")
patient = ObjectType("Patient")

root.set_field('gene', resolve_gene)
root.set_field('genes', resolve_genes)
root.set_field('tags', resolve_tags)
root.set_field('test', resolve_test)
root.set_field('mutation', resolve_mutation)
root.set_field('mutations', resolve_mutations)
root.set_field('patient', resolve_patient)


schema = make_executable_schema(
    type_defs, [root, gene, tag, mutation, patient])
