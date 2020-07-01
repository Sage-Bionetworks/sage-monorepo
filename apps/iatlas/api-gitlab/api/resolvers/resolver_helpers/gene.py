from sqlalchemy import and_, orm
from api import db
from api.database import return_gene_query
from api.db_models import (
    Dataset, DatasetToSample, Gene, GeneFamily, GeneFunction, GeneToSample, GeneType,
    ImmuneCheckpoint, Pathway, Publication, SuperCategory, Sample, SampleToTag, Tag,
    TagToTag, TherapyType)
from .general_resolvers import build_option_args, get_selection_set
from .tag import request_tags


def build_gene_request(_obj, info, data_set=None, related=None, gene_type=None, entrez=None, samples=None, by_tag=False):
    """
    Builds a SQL request and returns values from the DB.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, by_tag, child_node='genes')

    gene_1 = orm.aliased(Gene, name='g')
    gene_family_1 = orm.aliased(GeneFamily, name='gf')
    gene_function_1 = orm.aliased(GeneFunction, name='gfn')
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

    query = sess.query(gene_1)

    if 'gene_family' in relations:
        query = query.join((gene_family_1, gene_1.gene_family), isouter=True)
        option_args.append(orm.contains_eager(
            gene_1.gene_family.of_type(gene_family_1)))

    if 'gene_function' in relations:
        query = query.join(
            (gene_function_1, gene_1.gene_function), isouter=True)
        option_args.append(orm.contains_eager(
            gene_1.gene_function.of_type(gene_function_1)))

    if 'gene_types' in relations or gene_type:
        query = query.join((gene_type_1, gene_1.gene_types), isouter=True)
        option_args.append(orm.contains_eager(
            gene_1.gene_types.of_type(gene_type_1)))

    if 'immune_checkpoint' in relations:
        query = query.join(
            (immune_checkpoint_1, gene_1.immune_checkpoint), isouter=True)
        option_args.append(orm.contains_eager(
            gene_1.immune_checkpoint.of_type(immune_checkpoint_1)))

    if 'pathway' in relations:
        query = query.join((pathway_1, gene_1.pathway), isouter=True)
        option_args.append(orm.contains_eager(
            gene_1.pathway.of_type(pathway_1)))

    if 'publications' in relations:
        query = query.join((pub_1, gene_1.publications), isouter=True)
        option_args.append(orm.contains_eager(
            gene_1.publications.of_type(pub_1)))

    if 'super_category' in relations:
        query = query.join(
            (super_category_1, gene_1.super_category), isouter=True)
        option_args.append(orm.contains_eager(
            gene_1.super_category.of_type(pub_1)))

    if 'therapy_type' in relations:
        query = query.join((therapy_type_1, gene_1.therapy_type), isouter=True)
        option_args.append(orm.contains_eager(
            gene_1.therapy_type.of_type(therapy_type_1)))

    if option_args:
        query = query.options(*option_args)
    else:
        query = sess.query(*core)

    if gene_type:
        query = query.filter(gene_type_1.name.in_(gene_type))

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    if samples:
        sample_1 = orm.aliased(Sample, name='s')
        gene_to_sample_1 = orm.aliased(GeneToSample, name='gs')
        query = query.join(gene_to_sample_1,
                           and_(gene_1.id == gene_to_sample_1.gene_id,
                                gene_to_sample_1.sample_id.in_(
                                    sess.query(sample_1.id).filter(
                                        sample_1.name.in_(samples))
                                )))

    return query


def request_gene(_obj, info, entrez=None):
    if entrez:
        entrez = [entrez]
        query = build_gene_request(_obj, info, entrez=entrez)
        return query.one_or_none()
    return None


def request_genes(_obj, info, data_set=None, related=None, entrez=None, gene_type=None, samples=None, by_tag=False):
    query = build_gene_request(_obj, info, data_set=data_set, related=related,
                               entrez=entrez, gene_type=gene_type, samples=samples,
                               by_tag=by_tag)
    query = query.distinct()
    return query.all()
