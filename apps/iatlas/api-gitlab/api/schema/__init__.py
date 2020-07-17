from ariadne import load_schema_from_path, make_executable_schema, ObjectType, ScalarType
import os
import decimal
from api.resolvers import (
    resolve_copy_number_results, resolve_data_sets, resolve_driver_results, resolve_features,
    resolve_features_by_class, resolve_features_by_tag, resolve_gene, resolve_gene_types,
    resolve_genes, resolve_genes_by_tag, resolve_mutations, resolve_mutation_types, resolve_patients,
    resolve_related, resolve_samples, resolve_samples_by_tag, resolve_slides, resolve_tags,
    resolve_test)

schema_dirname, _filename = os.path.split(os.path.abspath(__file__))

# Import GraphQl schemas
root_query = load_schema_from_path(schema_dirname + '/root.query.graphql')
copy_number_result_query = load_schema_from_path(
    schema_dirname + '/copyNumberResult.query.graphql')
data_set_query = load_schema_from_path(
    schema_dirname + '/dataset.query.graphql')
driver_result_query = load_schema_from_path(
    schema_dirname + '/driverResult.query.graphql')
feature_query = load_schema_from_path(
    schema_dirname + '/feature.query.graphql')
gene_query = load_schema_from_path(schema_dirname + '/gene.query.graphql')
gene_type_query = load_schema_from_path(
    schema_dirname + '/gene_type.query.graphql')
mutation_query = load_schema_from_path(
    schema_dirname + '/mutation.query.graphql')
mutation_code_query = load_schema_from_path(
    schema_dirname + '/mutationCode.query.graphql')
patient_query = load_schema_from_path(
    schema_dirname + '/patient.query.graphql')
publication_query = load_schema_from_path(
    schema_dirname + '/publication.query.graphql')
sample_query = load_schema_from_path(schema_dirname + '/sample.query.graphql')
slide_query = load_schema_from_path(schema_dirname + '/slide.query.graphql')
tag_query = load_schema_from_path(schema_dirname + '/tag.query.graphql')

type_defs = [root_query, copy_number_result_query, data_set_query, driver_result_query, feature_query,
             gene_query, gene_type_query, mutation_query, mutation_code_query, patient_query,
             publication_query, sample_query, slide_query, tag_query]

# Initialize custom scalars.
direction_enum_scalar = ScalarType('DirectionEnum')


@direction_enum_scalar.serializer
def serialize_direction_enum(value):
    return value if value == 'Amp' or value == 'Del' else None


feature_value_scalar = ScalarType('FeatureValue')


@feature_value_scalar.serializer
def serialize_feature_value(value):
    if isinstance(value, decimal.Decimal):
        return float(value)
    elif isinstance(value, str):
        return value
    return None


# Initialize schema objects (general).
root = ObjectType('Query')
copy_number_result = ObjectType('CopyNumberResult')
data_set = ObjectType('DataSet')
driver_result = ObjectType('DriverResult')
feature = ObjectType('Feature')
features_by_class = ObjectType('FeaturesByClass')
features_by_tag = ObjectType('FeaturesByTag')
gene = ObjectType('Gene')
genes_by_tag = ObjectType('GenesByTag')
gene_type = ObjectType('GeneType')
mutation = ObjectType('Mutation')
mutation_code = ObjectType('MutationCode')
mutation_type = ObjectType('MutationType')
patient = ObjectType('Patient')
publication = ObjectType('Publication')
related_by_data_set = ObjectType('RelatedByDataSet')
sample = ObjectType('Sample')
sample_by_tag = ObjectType('SamplesByTag')
slide = ObjectType('Slide')
tag = ObjectType('Tag')

# Initialize schema objects (simple).
simple_data_set = ObjectType('SimpleDataSet')
simple_feature = ObjectType('SimpleFeature')
simple_gene = ObjectType('SimpleGene')
simple_gene_type = ObjectType('SimpleGeneType')
simple_publication = ObjectType('SimplePublication')
simple_tag = ObjectType('SimpleTag')

# Associate resolvers with fields.
root.set_field('copyNumberResults', resolve_copy_number_results)
root.set_field('dataSets', resolve_data_sets)
root.set_field('driverResults', resolve_driver_results)
root.set_field('features', resolve_features)
root.set_field('featuresByClass', resolve_features_by_class)
root.set_field('featuresByTag', resolve_features_by_tag)
root.set_field('gene', resolve_gene)
root.set_field('geneTypes', resolve_gene_types)
root.set_field('genes', resolve_genes)
root.set_field('genesByTag', resolve_genes_by_tag)
root.set_field('mutations', resolve_mutations)
root.set_field('mutationTypes', resolve_mutation_types)
root.set_field('patients', resolve_patients)
root.set_field('related', resolve_related)
root.set_field('samples', resolve_samples)
root.set_field('samplesByTag', resolve_samples_by_tag)
root.set_field('slides', resolve_slides)
root.set_field('tags', resolve_tags)
root.set_field('test', resolve_test)


schema = make_executable_schema(
    type_defs,
    [root, copy_number_result, data_set, direction_enum_scalar, driver_result, feature,
     features_by_class, features_by_tag, feature_value_scalar, gene, genes_by_tag, gene_type,
     mutation, mutation_code, mutation_type, patient, publication, related_by_data_set, sample,
     sample_by_tag, simple_data_set, simple_feature, simple_gene, simple_gene_type,
     simple_publication, simple_tag, slide, tag]
)
