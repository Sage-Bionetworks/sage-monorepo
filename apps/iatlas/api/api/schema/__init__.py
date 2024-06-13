from ariadne import load_schema_from_path, make_executable_schema, ObjectType, ScalarType
import os
from api.resolvers import (
    resolve_cohorts,
    resolve_colocalizations,
    resolve_copy_number_results,
    resolve_data_sets,
    resolve_driver_results,
    resolve_edges,
    resolve_features,
    resolve_gene_types,
    resolve_genes,
    resolve_germline_gwas_results,
    resolve_heritability_results,
    resolve_mutations,
    resolve_mutation_types,
    resolve_neoantigens,
    resolve_nodes,
    resolve_rare_variant_pathway_associations,
    resolve_patients,
    resolve_samples,
    resolve_slides,
    resolve_snps,
    resolve_tags,
    resolve_test,
    resolve_heritability_results
)


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
gene_query = load_schema_from_path(schema_dirname + '/gene.query.graphql')
gene_type_query = load_schema_from_path(
    schema_dirname + '/geneType.query.graphql')
germline_gwas_result_query = load_schema_from_path(
    schema_dirname + '/germlineGwasResult.query.graphql')
heritability_result_query = load_schema_from_path(
    schema_dirname + '/heritabilityResult.query.graphql')
mutation_query = load_schema_from_path(
    schema_dirname + '/mutation.query.graphql')
neoantigen_query = load_schema_from_path(
    schema_dirname + '/neoantigen.query.graphql')
node_query = load_schema_from_path(
    schema_dirname + '/node.query.graphql')
patient_query = load_schema_from_path(
    schema_dirname + '/patient.query.graphql')
publication_query = load_schema_from_path(
    schema_dirname + '/publication.query.graphql')
rare_variant_pathway_association_query = load_schema_from_path(
    schema_dirname + '/rareVariantPathwayAssociation.query.graphql')
sample_query = load_schema_from_path(schema_dirname + '/sample.query.graphql')
slide_query = load_schema_from_path(schema_dirname + '/slide.query.graphql')
snp_query = load_schema_from_path(schema_dirname + '/snp.query.graphql')
tag_query = load_schema_from_path(schema_dirname + '/tag.query.graphql')

type_defs = [
    root_query,
    paging_types,
    cohort_query,
    colocalization_query,
    copy_number_result_query,
    data_set_query,
    driver_result_query,
    edge_query,
    feature_query,
    gene_query,
    gene_type_query,
    germline_gwas_result_query,
    heritability_result_query,
    neoantigen_query,
    mutation_query,
    node_query,
    rare_variant_pathway_association_query,
    patient_query,
    publication_query,
    sample_query,
    slide_query,
    snp_query,
    tag_query
]

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
gene = ObjectType('Gene')
gene_type = ObjectType('GeneType')
germline_gwas_result_node = ObjectType('GermlineGwasResultNode')
germline_gwas_result = ObjectType('GermlineGwasResult')
heritability_result_node = ObjectType('HeritabilityResultNode')
heritability_result = ObjectType('HeritabilityResult')
mutation = ObjectType('Mutation')
mutation_type = ObjectType('MutationType')
neoantigen = ObjectType('Neoantigen')
node = ObjectType('Node')
node_result = ObjectType('NodeResult')
patient = ObjectType('Patient')
publication = ObjectType('Publication')
rare_variant_pathway_association = ObjectType(
    'RareVariantPathwayAssociationNode')
related_by_data_set = ObjectType('RelatedByDataSet')
sample = ObjectType('Sample')
sample_by_mutation_status = ObjectType('SampleByMutationStatus')
slide = ObjectType('Slide')
snp = ObjectType('Snp')
tag = ObjectType('Tag')

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
root.set_field('geneTypes', resolve_gene_types)
root.set_field('genes', resolve_genes)
root.set_field('germlineGwasResults', resolve_germline_gwas_results)
root.set_field('heritabilityResults', resolve_heritability_results)
root.set_field('mutations', resolve_mutations)
root.set_field('mutationTypes', resolve_mutation_types)
root.set_field('neoantigens', resolve_neoantigens)
root.set_field('nodes', resolve_nodes)
root.set_field('patients', resolve_patients)
root.set_field('rareVariantPathwayAssociations',
               resolve_rare_variant_pathway_associations)
root.set_field('samples', resolve_samples)
root.set_field('slides', resolve_slides)
root.set_field('snps', resolve_snps)
root.set_field('tags', resolve_tags)
root.set_field('test', resolve_test)


schema = make_executable_schema(
    type_defs,
    [
        root,
        cohort,
        colocalization,
        copy_number_result,
        data_set,
        direction_enum_scalar,
        driver_result,
        edge_result,
        ethnicity_enum_scalar,
        feature, gender_enum_scalar,
        gene,
        gene_type,
        germline_gwas_result,
        germline_gwas_result_node,
        heritability_result_node,
        heritability_result,
        mutation,
        mutation_type,
        neoantigen,
        node,
        node_result,
        patient,
        publication,
        race_enum_scalar,
        rare_variant_pathway_association,
        related_by_data_set,
        sample,
        sample_by_mutation_status,
        simple_data_set,
        simple_feature,
        simple_gene,
        simple_gene_type,
        simple_node,
        simple_publication,
        simple_tag,
        slide,
        snp,
        tag
    ]
)
