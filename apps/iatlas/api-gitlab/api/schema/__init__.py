from ariadne import load_schema_from_path, make_executable_schema, ObjectType, ScalarType
import os
from api.resolvers import (
    resolve_data_sets, resolve_features, resolve_features_by_class, resolve_features_by_tag,
    resolve_gene, resolve_genes, resolve_genes_by_tag, resolve_mutation, resolve_mutations,
    resolve_mutation_types, resolve_patient, resolve_patients, resolve_sample, resolve_samples,
    resolve_slide, resolve_slides, resolve_tags, resolve_test)

schema_dirname, _filename = os.path.split(os.path.abspath(__file__))

# Import GraphQl schemas
root_query = load_schema_from_path(schema_dirname + '/root.query.graphql')
data_set_query = load_schema_from_path(
    schema_dirname + '/dataset.query.graphql')
feature_query = load_schema_from_path(
    schema_dirname + '/feature.query.graphql')
gene_query = load_schema_from_path(schema_dirname + '/gene.query.graphql')
gene_type_query = load_schema_from_path(
    schema_dirname + '/gene_type.query.graphql')
mutation_query = load_schema_from_path(
    schema_dirname + '/mutation.query.graphql')
patient_query = load_schema_from_path(
    schema_dirname + '/patient.query.graphql')
publication_query = load_schema_from_path(
    schema_dirname + '/publication.query.graphql')
sample_query = load_schema_from_path(schema_dirname + '/sample.query.graphql')
slide_query = load_schema_from_path(schema_dirname + '/slide.query.graphql')
tag_query = load_schema_from_path(schema_dirname + '/tag.query.graphql')

type_defs = [root_query, data_set_query, feature_query, gene_query,
             gene_type_query, mutation_query, patient_query, publication_query,
             sample_query, slide_query, tag_query]

# Initialize custom scalars.
feature_value_type = ScalarType('FeatureValue')


@feature_value_type.serializer
def serialize_feature_value(value):
    if type(value) is str or type(value) is float:
        return value


# Initialize schema objects (general).
root = ObjectType('Query')
data_set = ObjectType('DataSet')
feature = ObjectType('Feature')
features_by_class = ObjectType('FeaturesByClass')
features_by_tag = ObjectType('FeaturesByTag')
gene = ObjectType('Gene')
genes_by_tag = ObjectType('GenesByTag')
gene_type = ObjectType('GeneType')
mutation = ObjectType('Mutation')
mutation_type = ObjectType('MutationType')
patient = ObjectType('Patient')
publication = ObjectType('Publication')
sample = ObjectType('Sample')
slide = ObjectType('Slide')
tag = ObjectType('Tag')

# Initialize schema objects (simple).
simple_data_set = ObjectType('SimpleDataSet')
simple_gene = ObjectType('SimpleGene')
simple_gene_type = ObjectType('SimpleGeneType')
simple_publication = ObjectType('SimplePublication')
simple_sample = ObjectType('SimpleSample')
simple_tag = ObjectType('SimpleTag')

# Associate resolvers with fields.
root.set_field('dataSets', resolve_data_sets)
root.set_field('features', resolve_features)
root.set_field('featuresByClass', resolve_features_by_class)
root.set_field('featuresByTag', resolve_features_by_tag)
root.set_field('gene', resolve_gene)
root.set_field('genes', resolve_genes)
root.set_field('genesByTag', resolve_genes_by_tag)
root.set_field('mutation', resolve_mutation)
root.set_field('mutations', resolve_mutations)
root.set_field('mutationTypes', resolve_mutation_types)
root.set_field('patient', resolve_patient)
root.set_field('patients', resolve_patients)
root.set_field('sample', resolve_sample)
root.set_field('samples', resolve_samples)
root.set_field('slide', resolve_slide)
root.set_field('slides', resolve_slides)
root.set_field('tags', resolve_tags)
root.set_field('test', resolve_test)


schema = make_executable_schema(
    type_defs,
    [root, data_set, feature, features_by_class, features_by_tag,
     feature_value_type, gene, genes_by_tag, gene_type, mutation,
     mutation_type, patient, publication, sample, simple_data_set,
     simple_gene, simple_gene_type, simple_publication, simple_sample,
     simple_tag, slide, tag]
)
