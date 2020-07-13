from sqlalchemy import and_, orm
from api import db
from api.database import return_gene_query
from api.db_models import (
    Dataset, DatasetToSample, Gene, GeneFamily, GeneFunction, GeneToSample, GeneType,
    ImmuneCheckpoint, Pathway, Publication, SuperCategory, Sample, SampleToTag, Tag,
    TagToTag, TherapyType)
from .general_resolvers import build_option_args, get_selection_set, get_value
from .tag import request_tags


def build_gene_to_sample_join_condition(gene_to_sample_model, gene_model, sample_model, samples=None):
    sess = db.session
    gene_to_sample_join_conditions = [
        gene_model.id == gene_to_sample_model.gene_id]
    if samples:
        gene_to_sample_join_conditions.append(gene_to_sample_model.sample_id.in_(
            sess.query(sample_model.id).filter(
                sample_model.name.in_(samples))))
    return gene_to_sample_join_conditions


def build_gene_request(_obj, info, gene_type=None, entrez=None, samples=None, by_tag=False):
    """
    Builds a SQL request and returns values from the DB.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, by_tag, child_node='genes')

    gene_1 = orm.aliased(Gene, name='g')
    gene_family_1 = orm.aliased(GeneFamily, name='gf')
    gene_function_1 = orm.aliased(GeneFunction, name='gfn')
    gene_to_sample_1 = orm.aliased(GeneToSample, name='gs')
    gene_type_1 = orm.aliased(GeneType, name='gt')
    immune_checkpoint_1 = orm.aliased(ImmuneCheckpoint, name='ic')
    pathway_1 = orm.aliased(Pathway, name='py')
    pub_1 = orm.aliased(Publication, name='p')
    super_category_1 = orm.aliased(SuperCategory, name='sc')
    tag_1 = orm.aliased(Tag, name='t')
    therapy_type_1 = orm.aliased(TherapyType, name='tht')

    core_field_mapping = {'entrez': gene_1.entrez.label('entrez'),
                          'hgnc': gene_1.hgnc.label('hgnc'),
                          'description': gene_1.description.label('description'),
                          'friendlyName': gene_1.friendly_name.label('friendly_name'),
                          'ioLandscapeName': gene_1.io_landscape_name.label('io_landscape_name')}

    related_field_mapping = {'geneFamily': 'gene_family',
                             'geneFunction': 'gene_function',
                             'geneTypes': 'gene_types',
                             'immuneCheckpoint': 'immune_checkpoint',
                             'pathway': 'pathway',
                             'publications': 'publications',
                             'rnaSeqExpr': 'rna_seq_expr',
                             'superCategory': 'super_category',
                             'therapyType': 'therapy_type'}

    core = build_option_args(selection_set, core_field_mapping)
    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []
    append_to_option_args = option_args.append

    query = sess.query(gene_1)

    if 'gene_family' in relations:
        query = query.join((gene_family_1, gene_1.gene_family), isouter=True)
        append_to_option_args(orm.contains_eager(
            gene_1.gene_family.of_type(gene_family_1)))

    if 'gene_function' in relations:
        query = query.join(
            (gene_function_1, gene_1.gene_function), isouter=True)
        append_to_option_args(orm.contains_eager(
            gene_1.gene_function.of_type(gene_function_1)))

    if 'gene_types' in relations or gene_type:
        query = query.join((gene_type_1, gene_1.gene_types), isouter=True)
        append_to_option_args(orm.contains_eager(
            gene_1.gene_types.of_type(gene_type_1)))

    if 'immune_checkpoint' in relations:
        query = query.join(
            (immune_checkpoint_1, gene_1.immune_checkpoint), isouter=True)
        append_to_option_args(orm.contains_eager(
            gene_1.immune_checkpoint.of_type(immune_checkpoint_1)))

    if 'pathway' in relations:
        query = query.join((pathway_1, gene_1.pathway), isouter=True)
        append_to_option_args(orm.contains_eager(
            gene_1.pathway.of_type(pathway_1)))

    if 'publications' in relations:
        query = query.join((pub_1, gene_1.publications), isouter=True)
        append_to_option_args(orm.contains_eager(
            gene_1.publications.of_type(pub_1)))

    if samples or 'rna_seq_expr' in relations:
        query = query.join(
            (gene_to_sample_1, gene_1.gene_sample_assoc), isouter=True)
        append_to_option_args(orm.contains_eager(
            gene_1.gene_sample_assoc.of_type(gene_to_sample_1)))
        if samples:
            sample_1 = orm.aliased(Sample, name='s')
            query = query.filter(gene_to_sample_1.sample_id.in_(sess.query(sample_1.id).filter(
                sample_1.name.in_(samples))))

    if 'super_category' in relations:
        query = query.join(
            (super_category_1, gene_1.super_category), isouter=True)
        append_to_option_args(orm.contains_eager(
            gene_1.super_category.of_type(pub_1)))

    if 'therapy_type' in relations:
        query = query.join((therapy_type_1, gene_1.therapy_type), isouter=True)
        append_to_option_args(orm.contains_eager(
            gene_1.therapy_type.of_type(therapy_type_1)))

    if option_args:
        query = query.options(*option_args)
    else:
        # Need some at least one column to select.
        if not core:
            core.append(gene_1.id)
        query = sess.query(*core)

    if gene_type:
        query = query.filter(gene_type_1.name.in_(gene_type))

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    return query


def get_rna_seq_expr(gene_row):
    rna_seq_expr = []
    append_to_rna_seq_expr = rna_seq_expr.append
    gene_sample_assoc = get_value(gene_row, 'gene_sample_assoc', [])
    for gene_sample_rel in gene_sample_assoc:
        rna_seq_expr_value = get_value(
            gene_sample_rel, 'rna_seq_expr')
        if rna_seq_expr_value:
            append_to_rna_seq_expr(rna_seq_expr_value)
    return rna_seq_expr


def request_gene(_obj, info, entrez=None):
    entrez = [entrez]
    query = build_gene_request(_obj, info, entrez=entrez)
    return query.one_or_none()


def request_genes(_obj, info, entrez=None, gene_type=None, samples=None, by_tag=False):
    query = build_gene_request(_obj, info, entrez=entrez, gene_type=gene_type,
                               samples=samples, by_tag=by_tag)
    # query = query.distinct()
    return query.all()
