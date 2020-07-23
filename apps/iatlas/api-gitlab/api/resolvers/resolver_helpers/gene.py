from sqlalchemy import and_, orm
from sqlalchemy.orm.attributes import set_committed_value
from itertools import chain
from api import db
from api.database import return_gene_query
from api.db_models import (
    Dataset, DatasetToSample, Gene, GeneFamily, GeneFunction, GeneToSample, GeneToType,
    GeneType, ImmuneCheckpoint, Pathway, Publication, PublicationToGeneToGeneType,
    SuperCategory, Sample, SampleToTag, Tag, TagToTag, TherapyType)
from .general_resolvers import build_join_condition, build_option_args, get_selection_set, get_value
from .tag import request_tags


def build_gene_graphql_response(gene_type_dict=dict(), pub_dict=dict(), sample_dict=dict()):
    def f(gene):
        gene_id = get_value(gene, 'id')
        return {
            'entrez': get_value(gene, 'entrez'),
            'hgnc': get_value(gene, 'hgnc'),
            'description': get_value(gene, 'description'),
            'friendlyName': get_value(gene, 'friendly_name'),
            'ioLandscapeName': get_value(gene, 'io_landscape_name'),
            'geneFamily': get_value(gene, 'gene_family'),
            'geneFunction': get_value(gene, 'gene_function'),
            'geneTypes': gene_type_dict.get(gene_id, []),
            'immuneCheckpoint': get_value(gene, 'immune_checkpoint'),
            'pathway': get_value(gene, 'pathway'),
            'publications': [{
                'doId': get_value(pub, 'do_id'),
                'firstAuthorLastName': get_value(pub, 'first_author_last_name'),
                'journal': get_value(pub, 'journal'),
                'name': get_value(pub),
                'pubmedId': get_value(pub, 'pubmed_id'),
                'title': get_value(pub, 'title'),
                'year': get_value(pub, 'year')
            } for pub in pub_dict.get(gene_id, [])],
            'samples': [{
                'name': get_value(sample),
                'rnaSeqExpr': get_value(sample, 'rna_seq_expr')
            } for sample in sample_dict.get(gene_id, [])],
            'superCategory': get_value(gene, 'super_category'),
            'therapyType': get_value(gene, 'therapy_type')
        }
    return f


def build_pub_gene_gene_type_join_condition(gene_dict, gene_types, pub_gene_gene_type_model, pub_model):
    pub_gene_gene_type_join_condition = [
        pub_gene_gene_type_model.publication_id == pub_model.id, pub_gene_gene_type_model.gene_id.in_([*gene_dict])]

    map_of_ids = list(map(lambda gt: gt.id, gene_types))
    chain_of_ids = chain.from_iterable(map_of_ids) if any(map_of_ids) else None
    gene_type_ids = set(chain_of_ids) if chain_of_ids else None

    if gene_type_ids:
        pub_gene_gene_type_join_condition.append(
            pub_gene_gene_type_model.gene_type_id.in_(gene_type_ids))

    return pub_gene_gene_type_join_condition


def build_gene_request(_obj, info, entrez=None, gene_family=None, gene_function=None, gene_type=None,
                       immune_checkpoint=None, pathway=None, super_category=None, therapy_type=None,
                       sample=None, by_tag=False):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, by_tag, child_node='genes')

    gene_1 = orm.aliased(Gene, name='g')
    gene_family_1 = orm.aliased(GeneFamily, name='gf')
    gene_function_1 = orm.aliased(GeneFunction, name='gfn')
    gene_to_sample_1 = orm.aliased(GeneToSample, name='gs')
    gene_to_type_1 = orm.aliased(GeneToType, name='ggt')
    gene_type_1 = orm.aliased(GeneType, name='gt')
    immune_checkpoint_1 = orm.aliased(ImmuneCheckpoint, name='ic')
    pathway_1 = orm.aliased(Pathway, name='py')
    sample_1 = orm.aliased(Sample, name='s')
    super_category_1 = orm.aliased(SuperCategory, name='sc')
    therapy_type_1 = orm.aliased(TherapyType, name='tht')

    core_field_mapping = {'entrez': gene_1.entrez.label('entrez'),
                          'hgnc': gene_1.hgnc.label('hgnc'),
                          'description': gene_1.description.label('description'),
                          'friendlyName': gene_1.friendly_name.label('friendly_name'),
                          'ioLandscapeName': gene_1.io_landscape_name.label('io_landscape_name'),
                          'geneFamily': gene_family_1.name.label('gene_family'),
                          'geneFunction': gene_function_1.name.label('gene_function'),
                          'immuneCheckpoint': immune_checkpoint_1.name.label('immune_checkpoint'),
                          'pathway': pathway_1.name.label('pathway'),
                          'superCategory': super_category_1.name.label('super_category'),
                          'therapyType': therapy_type_1.name.label('therapy_type')}
    related_field_mapping = {'geneFamily': 'gene_family',
                             'geneFunction': 'gene_function',
                             'geneTypes': 'gene_types',
                             'immuneCheckpoint': 'immune_checkpoint',
                             'pathway': 'pathway',
                             'samples': 'samples',
                             'superCategory': 'super_category',
                             'therapyType': 'therapy_type'}

    relations = build_option_args(selection_set, related_field_mapping)
    core = build_option_args(selection_set, core_field_mapping)
    append_to_core = core.append
    append_to_core(gene_1.id)
    option_args = []
    append_to_option_args = option_args.append

    query = sess.query(*core)
    query = query.select_from(gene_1)

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    if gene_type:
        query = query.join(gene_to_type_1, and_(
            gene_to_type_1.gene_id == gene_1.id, gene_to_type_1.type_id.in_(sess.query(gene_type_1.id).filter(gene_type_1.name.in_(gene_type)))))

    if sample:
        query = query.join(gene_to_sample_1, and_(gene_to_sample_1.gene_id == gene_1.id,
                                                  gene_to_sample_1.sample_id.in_(sess.query(sample_1.id).filter(sample_1.name.in_(sample)))))

    if 'gene_family' in relations or gene_family:
        is_outer = not bool(gene_family)
        gene_family_join_condition = build_join_condition(
            gene_family_1.id, gene_1.gene_family_id, filter_column=gene_family_1.name, filter_list=gene_family)
        query = query.join(gene_family_1, and_(
            *gene_family_join_condition), isouter=is_outer)

    if 'gene_function' in relations or gene_function:
        is_outer = not bool(gene_function)
        gene_function_join_condition = build_join_condition(
            gene_function_1.id, gene_1.gene_function_id, filter_column=gene_function_1.name, filter_list=gene_function)
        query = query.join(gene_function_1, and_(
            *gene_function_join_condition), isouter=is_outer)

    if 'immune_checkpoint' in relations or immune_checkpoint:
        is_outer = not bool(immune_checkpoint)
        immune_checkpoint_join_condition = build_join_condition(
            immune_checkpoint_1.id, gene_1.immune_checkpoint_id, filter_column=immune_checkpoint_1.name, filter_list=immune_checkpoint)
        query = query.join(immune_checkpoint_1, and_(
            *immune_checkpoint_join_condition), isouter=is_outer)

    if 'pathway' in relations or pathway:
        is_outer = not bool(pathway)
        pathway_join_condition = build_join_condition(
            pathway_1.id, gene_1.pathway_id, filter_column=pathway_1.name, filter_list=pathway)
        query = query.join(pathway_1, and_(
            *pathway_join_condition), isouter=is_outer)

    if 'super_category' in relations or super_category:
        is_outer = not bool(super_category)
        super_category_join_condition = build_join_condition(
            super_category_1.id, gene_1.super_cat_id, filter_column=super_category_1.name, filter_list=super_category)
        query = query.join(super_category_1, and_(
            *super_category_join_condition), isouter=is_outer)

    if 'therapy_type' in relations or therapy_type:
        is_outer = not bool(therapy_type)
        therapy_type_join_condition = build_join_condition(
            therapy_type_1.id, gene_1.therapy_type_id, filter_column=therapy_type_1.name, filter_list=therapy_type)
        query = query.join(therapy_type_1, and_(
            *therapy_type_join_condition), isouter=is_outer)
    return query


def request_gene(_obj, info, entrez=None, sample=None):
    query = build_gene_request(_obj, info, entrez=[entrez], sample=sample)
    return query.one_or_none()


def request_genes(_obj, info, entrez=None, gene_type=None, sample=None, by_tag=False):
    genes_query = build_gene_request(_obj, info, entrez=entrez, gene_type=gene_type,
                                     sample=sample, by_tag=by_tag)
    return genes_query.distinct().all()


def get_gene_types(info, gene_type=None, gene_dict=dict()):
    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)
    relations = build_option_args(
        selection_set, {'geneTypes': 'gene_types'})

    if 'gene_types' in relations:
        sess = db.session
        gene_type_1 = orm.aliased(GeneType, name='gt')
        gene_to_gene_type_1 = orm.aliased(GeneToType, name='ggt')

        gene_type_selection_set = get_selection_set(
            selection_set, ('gene_types' in relations), child_node='geneTypes')
        gene_type_core_field_mapping = {'name': gene_type_1.name.label('name'),
                                        'display': gene_type_1.display.label('display')}

        gene_type_core = build_option_args(
            gene_type_selection_set, gene_type_core_field_mapping)
        gene_type_core = gene_type_core + [gene_type_1.id.label('id'),
                                           gene_to_gene_type_1.gene_id.label('gene_id')]

        gene_type_query = sess.query(*gene_type_core)
        gene_type_query = gene_type_query.select_from(gene_type_1)

        gene_gene_type_join_condition = build_join_condition(
            gene_to_gene_type_1.type_id, gene_type_1.id, gene_to_gene_type_1.gene_id, [*gene_dict])

        if gene_type:
            gene_gene_type_join_condition.append(
                gene_type_1.name.in_(gene_type))

        gene_type_query = gene_type_query.join(gene_to_gene_type_1, and_(
            *gene_gene_type_join_condition))

        return gene_type_query.distinct().all()

    return []


def get_publications(info, gene_types=[], gene_dict=dict(), by_tag=False):
    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, by_tag, child_node='genes')
    relations = build_option_args(
        selection_set, {'publications': 'publications'})

    if 'publications' in relations:
        sess = db.session
        gene_type_1 = orm.aliased(GeneType, name='gt')
        pub_1 = orm.aliased(Publication, name='p')
        pub_gene_gene_type_1 = orm.aliased(
            PublicationToGeneToGeneType, name='pggt')

        pub_selection_set = get_selection_set(
            selection_set, ('publications' in relations), child_node='publications')
        pub_core_field_mapping = {'doId': pub_1.do_id.label('do_id'),
                                  'firstAuthorLastName': pub_1.first_author_last_name.label('first_author_last_name'),
                                  'journal': pub_1.journal.label('journal'),
                                  'name': pub_1.name.label('name'),
                                  'pubmedId': pub_1.pubmed_id.label('pubmed_id'),
                                  'title': pub_1.title.label('title'),
                                  'year': pub_1.year.label('year')}

        pub_core = build_option_args(pub_selection_set, pub_core_field_mapping)

        pub_query = sess.query(
            *pub_core, pub_gene_gene_type_1.gene_id.label('gene_id'))
        pub_query = pub_query.select_from(pub_1)

        pub_gene_gene_type_join_condition = build_pub_gene_gene_type_join_condition(
            gene_dict, gene_types, pub_gene_gene_type_1, pub_1)
        pub_query = pub_query.join(pub_gene_gene_type_1, and_(
            *pub_gene_gene_type_join_condition))

        return pub_query.distinct().all()

    return []


def get_samples(info, sample=None, gene_dict=dict()):
    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)
    relations = build_option_args(selection_set, {'samples': 'samples'})

    if 'samples' in relations:
        sess = db.session
        sample_1 = orm.aliased(Sample, name='s')
        gene_to_sample_1 = orm.aliased(GeneToSample, name='gs')

        sample_selection_set = get_selection_set(
            selection_set, ('samples' in relations), child_node='samples')
        sample_core_field_mapping = {'name': sample_1.name.label('name'),
                                     'rnaSeqExpr': gene_to_sample_1.rna_seq_expr.label('rna_seq_expr')}

        sample_core = build_option_args(
            sample_selection_set, sample_core_field_mapping)
        sample_core = sample_core + [sample_1.id.label('id')]

        sample_query = sess.query(
            *sample_core, gene_to_sample_1.gene_id.label('gene_id'))
        sample_query = sample_query.select_from(sample_1)

        gene_sample_join_condition = build_join_condition(
            gene_to_sample_1.sample_id, sample_1.id, gene_to_sample_1.gene_id, [*gene_dict])

        if sample:
            gene_sample_join_condition.append(
                sample_1.name.in_(sample))

        sample_query = sample_query.join(gene_to_sample_1, and_(
            *gene_sample_join_condition))

        return sample_query.distinct().all()

    return []
