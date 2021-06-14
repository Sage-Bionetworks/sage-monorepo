from ariadne import load_schema_from_path, make_executable_schema, ObjectType, ScalarType
import os
from api.resolvers import (
    resolve_cohorts, resolve_colocalizations, resolve_copy_number_results, resolve_data_sets, resolve_driver_results, resolve_edges, resolve_features, resolve_feature_values, resolve_gene_family, resolve_gene_function, resolve_gene_types, resolve_genes, resolve_germline_gwas_results, resolve_heritability_results, resolve_immune_checkpoints, resolve_method_tags, resolve_mutations, resolve_mutations_by_sample, resolve_mutation_types, resolve_nodes, resolve_rare_variant_pathway_associations, resolve_pathways, resolve_patients, resolve_related, resolve_samples, resolve_samples_by_mutations_status, resolve_samples_by_tag, resolve_slides, resolve_snps, resolve_super_categories, resolve_tags, resolve_test, resolve_therapy_types)


schema_dirname, _filename = os.path.split(os.path.abspath(__file__))

# Import GraphQl schemas/
root_query = load_schema_from_path(schema_dirname + '/root.query.graphql')
paging_types = load_schema_from_path(
    schema_dirname + '/paging.graphql')
cohort_query = load_schema_from_path(
    schema_dirname + '/cohort.query.graphql')
colocalization_query = load_schema_from_path(
    schema_dirname + '/colocalization.query.graphql')
copy_number_result_query = load_schema_from_path(
    schema_dirname + '/copyNumberResult.query.graphql')
data_set_query = load_schema_from_path(
    schema_dirname + '/dataset.query.graphql')
driver_result_query = load_schema_from_path(
    schema_dirname + '/driverResult.query.graphql')
edge_query = load_schema_from_path(
    schema_dirname + '/edge.query.graphql')
feature_query = load_schema_from_path(
    schema_dirname + '/feature.query.graphql')
feature_value_query = load_schema_from_path(
    schema_dirname + '/featureValue.query.graphql')
gene_query = load_schema_from_path(schema_dirname + '/gene.query.graphql')
gene_family_query = load_schema_from_path(
    schema_dirname + '/geneFamily.query.graphql')
gene_function_query = load_schema_from_path(
    schema_dirname + '/geneFunction.query.graphql')
gene_type_query = load_schema_from_path(
    schema_dirname + '/geneType.query.graphql')
germline_gwas_result_query = load_schema_from_path(
    schema_dirname + '/germlineGwasResult.query.graphql')
heritability_result_query = load_schema_from_path(
    schema_dirname + '/heritabilityResult.query.graphql')
immune_checkpoint_query = load_schema_from_path(
    schema_dirname + '/immuneCheckpoint.query.graphql')
method_tag_query = load_schema_from_path(
    schema_dirname + '/methodTag.query.graphql')
mutation_query = load_schema_from_path(
    schema_dirname + '/mutation.query.graphql')
mutation_code_query = load_schema_from_path(
    schema_dirname + '/mutationCode.query.graphql')
node_query = load_schema_from_path(
    schema_dirname + '/node.query.graphql')
pathway_query = load_schema_from_path(
    schema_dirname + '/pathway.query.graphql')
patient_query = load_schema_from_path(
    schema_dirname + '/patient.query.graphql')
publication_query = load_schema_from_path(
    schema_dirname + '/publication.query.graphql')
rare_variant_pathway_association_query = load_schema_from_path(
    schema_dirname + '/rareVariantPathwayAssociation.query.graphql')
sample_query = load_schema_from_path(schema_dirname + '/sample.query.graphql')
slide_query = load_schema_from_path(schema_dirname + '/slide.query.graphql')
snp_query = load_schema_from_path(schema_dirname + '/snp.query.graphql')
super_category = load_schema_from_path(
    schema_dirname + '/superCategory.query.graphql')
tag_query = load_schema_from_path(schema_dirname + '/tag.query.graphql')
therapy_type_query = load_schema_from_path(
    schema_dirname + '/therapyType.query.graphql')

type_defs = [
    root_query, paging_types, cohort_query, colocalization_query, copy_number_result_query, data_set_query, driver_result_query, edge_query, feature_query, feature_value_query, gene_query, gene_family_query, gene_function_query, gene_type_query, germline_gwas_result_query, heritability_result_query, immune_checkpoint_query, method_tag_query, mutation_query, mutation_code_query, node_query, rare_variant_pathway_association_query, pathway_query, patient_query, publication_query, sample_query, slide_query, snp_query, super_category, tag_query, therapy_type_query]

# Initialize custom scalars.
direction_enum_scalar = ScalarType('DirectionEnum')


@direction_enum_scalar.serializer
def serialize_direction_enum(value):
    return value if value == 'Amp' or value == 'Del' else None


ethnicity_enum_scalar = ScalarType('EthnicityEnum')


@ethnicity_enum_scalar.serializer
def serialize_ethnicity_enum(value):
    return value if value == 'not hispanic or latino' or value == 'hispanic or latino' else None


gender_enum_scalar = ScalarType('GenderEnum')


@gender_enum_scalar.serializer
def serialize_gender_enum(value):
    return value if value == 'female' or value == 'male' else None


race_enum_scalar = ScalarType('RaceEnum')


@race_enum_scalar.serializer
def serialize_race_enum(value):
    race_set = {
        'american indian or alaska native',
        'native hawaiian or other pacific islander',
        'black or african american',
        'asian',
        'white'
    }
    return value if value in race_set else None


status_enum_scalar = ScalarType('StatusEnum')


@status_enum_scalar.serializer
def serialize_status_enum(value):
    return value if value == 'Mut' or value == 'Wt' else None


qtl_type_enum = ScalarType('QTLTypeEnum')


@qtl_type_enum.serializer
def serialize_qtl_type_enum(value):
    return value if value == 'sQTL' or value == 'eQTL' else None


ecaviar_pp_enum = ScalarType('ECaviarPPEnum')


@ecaviar_pp_enum.serializer
def serialize_ecaviar_pp_enum(value):
    return value if value == 'C1' or value == 'C2' else None


coloc_plot_type_enum = ScalarType('ColocPlotTypeEnum')


@coloc_plot_type_enum.serializer
def serialize_coloc_plot_type_enum(value):
    return value if value == '3 Level Plot' or value == 'Expanded Region' else None


# Initialize schema objects (general).
root = ObjectType('Query')
cohort = ObjectType('Cohort')
colocalization = ObjectType('Colocalization')
copy_number_result = ObjectType('CopyNumberResult')
data_set = ObjectType('DataSet')
driver_result = ObjectType('DriverResult')
edge_result = ObjectType('EdgeResult')
feature = ObjectType('Feature')
feature_value = ObjectType('FeatureValue')
gene = ObjectType('Gene')
gene_family = ObjectType('GeneFamily')
gene_function = ObjectType('GeneFunction')
gene_type = ObjectType('GeneType')
germline_gwas_result_node = ObjectType('GermlineGwasResultNode')
germline_gwas_result = ObjectType('GermlineGwasResult')
heritability_result_node = ObjectType('HeritabilityResultNode')
heritability_result = ObjectType('HeritabilityResult')
immune_checkpoint = ObjectType('ImmuneCheckpoint')
method_tag = ObjectType('MethodTag')
mutation = ObjectType('GeneMutation')
mutation_code = ObjectType('MutationCode')
mutation_type = ObjectType('MutationType')
node = ObjectType('Node')
node_result = ObjectType('NodeResult')
pathway = ObjectType('Pathway')
patient = ObjectType('Patient')
publication = ObjectType('Publication')
rare_variant_pathway_association = ObjectType(
    'RareVariantPathwayAssociationNode')
related_by_data_set = ObjectType('RelatedByDataSet')
sample = ObjectType('Sample')
sample_by_mutation_status = ObjectType('SampleByMutationStatus')
sample_by_tag = ObjectType('SamplesByTag')
slide = ObjectType('Slide')
snp = ObjectType('Snp')
super_category = ObjectType('SuperCategory')
tag = ObjectType('Tag')
therapy_type = ObjectType('TherapyType')

# Initialize schema objects (simple).
simple_data_set = ObjectType('SimpleDataSet')
simple_feature = ObjectType('SimpleFeature')
simple_gene = ObjectType('SimpleGene')
simple_gene_type = ObjectType('SimpleGeneType')
simple_node = ObjectType('SimpleNode')
simple_publication = ObjectType('SimplePublication')
simple_tag = ObjectType('SimpleTag')

'''
Associate resolvers with fields.
Fields should be names of objects in schema/root.query.graphql.
Values should be names of functions in resolvers
'''
root.set_field('cohorts', resolve_cohorts)
root.set_field('colocalizations', resolve_colocalizations)
root.set_field('copyNumberResults', resolve_copy_number_results)
root.set_field('dataSets', resolve_data_sets)
root.set_field('driverResults', resolve_driver_results)
root.set_field('edges', resolve_edges)
root.set_field('features', resolve_features)
root.set_field('featureValues', resolve_feature_values)
root.set_field('geneFamilies', resolve_gene_family)
root.set_field('geneFunctions', resolve_gene_function)
root.set_field('geneTypes', resolve_gene_types)
root.set_field('genes', resolve_genes)
root.set_field('germlineGwasResults', resolve_germline_gwas_results)
root.set_field('heritabilityResults', resolve_heritability_results)
root.set_field('immuneCheckpoints', resolve_immune_checkpoints)
root.set_field('methodTags', resolve_method_tags)
root.set_field('mutations', resolve_mutations)
root.set_field('mutationsBySample', resolve_mutations_by_sample)
root.set_field('mutationTypes', resolve_mutation_types)
root.set_field('nodes', resolve_nodes)
root.set_field('pathways', resolve_pathways)
root.set_field('patients', resolve_patients)
root.set_field('related', resolve_related)
root.set_field('rareVariantPathwayAssociations',
               resolve_rare_variant_pathway_associations)
root.set_field('samples', resolve_samples)
root.set_field('samplesByMutationStatus', resolve_samples_by_mutations_status)
root.set_field('samplesByTag', resolve_samples_by_tag)
root.set_field('slides', resolve_slides)
root.set_field('snps', resolve_snps)
root.set_field('superCategories', resolve_super_categories)
root.set_field('tags', resolve_tags)
root.set_field('test', resolve_test)
root.set_field('therapyTypes', resolve_therapy_types)


schema = make_executable_schema(
    type_defs,
    [
        root, cohort, colocalization, copy_number_result, data_set, direction_enum_scalar, driver_result, edge_result, ethnicity_enum_scalar, feature, feature_value, gender_enum_scalar, gene, gene_family, gene_function, gene_type, germline_gwas_result, germline_gwas_result_node, heritability_result_node, heritability_result, immune_checkpoint, method_tag, mutation, mutation_code, mutation_type, node, node_result, pathway, patient, publication, race_enum_scalar, rare_variant_pathway_association, related_by_data_set, sample, sample_by_mutation_status, sample_by_tag, simple_data_set, simple_feature, simple_gene, simple_gene_type, simple_node, simple_publication, simple_tag, slide, snp, tag, super_category, therapy_type]
)
