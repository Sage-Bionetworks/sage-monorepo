from ariadne import load_schema_from_path, make_executable_schema, ObjectType
import os
from flaskr.resolvers import (
    resolve_gene, resolve_genes, resolve_features,
    resolve_features_by_class, resolve_mutation, resolve_mutations, resolve_patient, resolve_patients,
    resolve_slide, resolve_slides, resolve_tags, resolve_test)

dirname, _filename = os.path.split(os.path.abspath(__file__))


root_query = load_schema_from_path(dirname + "/root.query.graphql")
gene_query = load_schema_from_path(dirname + "/gene.query.graphql")
feature_query = load_schema_from_path(dirname + "/feature.query.graphql")
sample_query = load_schema_from_path(dirname + "/sample.query.graphql")
tag_query = load_schema_from_path(dirname + "/tag.query.graphql")
mutation_query = load_schema_from_path(dirname + "/mutation.query.graphql")
patient_query = load_schema_from_path(dirname + "/patient.query.graphql")
slide_query = load_schema_from_path(dirname + "/slide.query.graphql")

type_defs = [root_query, gene_query, feature_query, mutation_query, patient_query, sample_query, tag_query, slide_query]


root = ObjectType("Query")
gene = ObjectType("Gene")
feature = ObjectType("Feature")
feature_by_class = ObjectType("FeatureByClass")
sample = ObjectType("Sample")
tag = ObjectType("Tag")
mutation = ObjectType("Mutation")
patient = ObjectType("Patient")
tag_as_child = ObjectType("TagAsChild")
slide = ObjectType("Slide")

root.set_field('gene', resolve_gene)
root.set_field('genes', resolve_genes)
root.set_field('features', resolve_features)
root.set_field('featuresByClass', resolve_features_by_class)
root.set_field('tags', resolve_tags)
root.set_field('test', resolve_test)
root.set_field('mutation', resolve_mutation)
root.set_field('mutations', resolve_mutations)
root.set_field('patient', resolve_patient)
root.set_field('patients', resolve_patients)
root.set_field('slide', resolve_slide)


schema = make_executable_schema(
    type_defs, [root, gene, feature, feature_by_class, mutation, patient, sample, tag, tag_as_child, slide])
