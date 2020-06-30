from ariadne import load_schema_from_path, make_executable_schema, ObjectType, ScalarType
import os
from api.resolvers import (
    resolve_gene, resolve_genes, resolve_features,
    resolve_features_by_class, resolve_features_by_tag,
    resolve_tags, resolve_test, resolve_driver_results)

dirname, _filename = os.path.split(os.path.abspath(__file__))

# Import GraphQl schemas
root_query = load_schema_from_path(dirname + '/root.query.graphql')
driver_result_query = load_schema_from_path(dirname + '/driverResult.query.graphql')
feature_query = load_schema_from_path(dirname + '/feature.query.graphql')
gene_query = load_schema_from_path(dirname + '/gene.query.graphql')
gene_type_query = load_schema_from_path(dirname + '/gene_type.query.graphql')
publication_query = load_schema_from_path(
    dirname + '/publication.query.graphql')
sample_query = load_schema_from_path(dirname + '/sample.query.graphql')
tag_query = load_schema_from_path(dirname + '/tag.query.graphql')

type_defs = [root_query, driver_result_query, gene_query, gene_type_query,
             feature_query, publication_query, sample_query, tag_query]

# Initialize custom scalars.
feature_value_type = ScalarType('FeatureValue')


@feature_value_type.serializer
def serialize_feature_value(value):
    if type(value) is str or type(value) is float:
        return value


# Initialize schema objects (general).
root = ObjectType('Query')
driver_result = ObjectType('DriverResult')
feature = ObjectType('Feature')
feature_by_class = ObjectType('FeatureByClass')
feature_by_class = ObjectType('FeatureByTag')
gene = ObjectType('Gene')
gene_type = ObjectType('GeneType')
publication = ObjectType('Publication')
sample = ObjectType('Sample')
tag = ObjectType('Tag')
# Initialize schema objects (simple).
simple_gene = ObjectType('SimpleGene')
simple_gene_type = ObjectType('SimpleGeneType')
simple_publication = ObjectType('SimplePublication')
simple_tag = ObjectType('SimpleTag')

# Associate resolvers with fields.
root.set_field('driverResults', resolve_driver_results)
root.set_field('gene', resolve_gene)
root.set_field('genes', resolve_genes)
root.set_field('features', resolve_features)
root.set_field('featuresByClass', resolve_features_by_class)
root.set_field('featuresByTag', resolve_features_by_tag)
root.set_field('tags', resolve_tags)
root.set_field('test', resolve_test)


schema = make_executable_schema(
    type_defs,
    [root, driver_result, gene, gene_type, feature, feature_by_class,
     feature_value_type, publication, sample, simple_gene,
     simple_gene_type, simple_publication, simple_tag, tag]
)
