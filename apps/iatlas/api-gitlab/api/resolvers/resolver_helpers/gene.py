from sqlalchemy import and_, func
from sqlalchemy.orm import aliased
from sqlalchemy.dialects.postgresql import aggregate_order_by
from itertools import groupby
from api import db
from api.db_models import (
    Dataset, DatasetToTag, DatasetToSample, Feature, FeatureClass, FeatureToSample, Gene, GeneFamily,
    GeneFunction, GeneToSample, GeneToType, GeneType, ImmuneCheckpoint, Pathway, Publication,
    PublicationToGeneToGeneType, SuperCategory, Sample, SampleToTag, Tag, TagToTag, TherapyType)
from .general_resolvers import build_join_condition, get_selected, get_value
from .publication import build_publication_graphql_response
from .paging_utils import get_pagination_queries, fetch_page
import logging


simple_gene_request_fields = {'entrez',
                              'hgnc',
                              'description',
                              'friendlyName',
                              'ioLandscapeName'}

gene_request_fields = simple_gene_request_fields.union({'geneFamily',
                                                        'geneFunction',
                                                        'geneTypes',
                                                        'immuneCheckpoint',
                                                        'pathway',
                                                        'publications',
                                                        'rnaSeqExprs',
                                                        'samples',
                                                        'superCategory',
                                                        'therapyType'})


def build_gene_graphql_response(pub_dict=dict(), gene_type_dict=dict()):
    def f(gene):
        if not gene:
            return None
        gene_id = get_value(gene, 'id')
        gene_types = gene_type_dict.get(gene_id, []) if gene_type_dict else []
        publications = pub_dict.get(gene_id, []) if pub_dict else []
        return {
            'id': gene_id,
            'entrez': get_value(gene, 'entrez'),
            'hgnc': get_value(gene, 'hgnc'),
            'description': get_value(gene, 'description'),
            'friendlyName': get_value(gene, 'friendly_name'),
            'ioLandscapeName': get_value(gene, 'io_landscape_name'),
            'geneFamily': get_value(gene, 'gene_family'),
            'geneFunction': get_value(gene, 'gene_function'),
            'geneTypes': gene_types,
            'immuneCheckpoint': get_value(gene, 'immune_checkpoint'),
            'pathway': get_value(gene, 'pathway'),
            'publications': map(build_publication_graphql_response, publications),
            'rnaSeqExprs': get_value(gene, 'rna_seq_exprs'),
            'samples': get_value(gene, 'samples'),
            'superCategory': get_value(gene, 'super_category'),
            'therapyType': get_value(gene, 'therapy_type')
        }
    return f


def build_pub_gene_gene_type_join_condition(gene_ids, gene_type, pub_gene_gene_type_model, pub_model):
    join_condition = build_join_condition(
        pub_gene_gene_type_model.publication_id, pub_model.id, pub_gene_gene_type_model.gene_id, gene_ids)

    if gene_type:
        gene_type_1 = aliased(GeneType, name='gt')
        gene_type_subquery = db.session.query(gene_type_1.id).filter(
            gene_type_1.name.in_(gene_type))
        join_condition.append(
            pub_gene_gene_type_model.gene_type_id.in_(gene_type_subquery))

    return join_condition


def build_gene_request(
        requested, tag_requested, distinct=False, paging=None, data_set=None, entrez=None, gene_family=None, gene_function=None, gene_type=None, immune_checkpoint=None, max_rna_seq_expr=None, min_rna_seq_expr=None, pathway=None, related=None, sample=None, super_category=None, tag=None, therapy_type=None):
    '''
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'tag' node of the graphql request. If 'tag' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `entrez` - a list of integers, gene entrez ids
        `gene_family` - a list of strings, gene family names
        `gene_function` - a list of strings, gene function names
        `gene_type` - a list of strings, gene type names
        `immune_checkpoint` - a list of strings, immune checkpoint names
        `max_rna_seq_expr` - a float, a maximum RNA Sequence Expression value
        `min_rna_seq_expr` - a float, a minimum RNA Sequence Expression value
        `pathway` - a list of strings, pathway names
        'paging' - an instance of PagingInput
            `type` - a string, the type of pagination to perform. Must be either 'OFFSET' or 'CURSOR'."
            `page` - an integer, when performing OFFSET paging, the page number requested.
            `limit` - an integer, when performing OFFSET paging, the number or records requested.
            `first` - an integer, when performing CURSOR paging, the number of records requested AFTER the CURSOR.
            `last` - an integer, when performing CURSOR paging, the number of records requested BEFORE the CURSOR.
            `before` - an integer, when performing CURSOR paging: the CURSOR to be used in tandem with 'last'
            `after` - an integer, when performing CURSOR paging: the CURSOR to be used in tandem with 'first'
        `related` - a list of strings, tag names related to data sets
        `sample` - a list of strings, sample names
        `super_category` - a list of strings, super category names
        `tag` - a list of strings, tag names related to samples
        `therapy_type` - a list of strings, therapy type names
    '''
    sess = db.session

    gene_1 = aliased(Gene, name='g')
    gene_family_1 = aliased(GeneFamily, name='gf')
    gene_function_1 = aliased(GeneFunction, name='gfn')
    gene_to_sample_1 = aliased(GeneToSample, name='gs')
    gene_to_type_1 = aliased(GeneToType, name='ggt')
    gene_type_1 = aliased(GeneType, name='gt')
    immune_checkpoint_1 = aliased(ImmuneCheckpoint, name='ic')
    pathway_1 = aliased(Pathway, name='py')
    sample_1 = aliased(Sample, name='s')
    super_category_1 = aliased(SuperCategory, name='sc')
    tag_1 = aliased(Tag, name='t')
    therapy_type_1 = aliased(TherapyType, name='tht')

    core_field_mapping = {'entrez': gene_1.entrez.label('entrez'),
                          'hgnc': gene_1.hgnc.label('hgnc'),
                          'description': gene_1.description.label('description'),
                          'friendlyName': gene_1.friendly_name.label('friendly_name'),
                          'ioLandscapeName': gene_1.io_landscape_name.label('io_landscape_name'),
                          'geneFamily': gene_family_1.name.label('gene_family'),
                          'geneFunction': gene_function_1.name.label('gene_function'),
                          'immuneCheckpoint': immune_checkpoint_1.name.label('immune_checkpoint'),
                          'pathway': pathway_1.name.label('pathway'),
                          'rnaSeqExprs': func.array_agg(aggregate_order_by(gene_to_sample_1.rna_seq_expr, gene_to_sample_1.sample_id.asc())).label('rna_seq_exprs'),
                          'samples': func.array_agg(aggregate_order_by(sample_1.name, sample_1.id.asc())).label('samples'),
                          'superCategory': super_category_1.name.label('super_category'),
                          'therapyType': therapy_type_1.name.label('therapy_type')}
    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                              'color': tag_1.color.label('color'),
                              'longDisplay': tag_1.long_display.label('tag_long_display'),
                              'shortDisplay': tag_1.short_display.label('tag_short_display'),
                              'tag': tag_1.name.label('tag')}

    core = get_selected(requested, core_field_mapping)
    core.add(gene_1.id)
    core |= get_selected(tag_requested, tag_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(gene_1)

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    if gene_type:
        query = query.join(gene_to_type_1, and_(
            gene_to_type_1.gene_id == gene_1.id, gene_to_type_1.type_id.in_(sess.query(gene_type_1.id).filter(gene_type_1.name.in_(gene_type)))))

    if 'geneFamily' in requested or gene_family:
        is_outer = not bool(gene_family)
        gene_family_join_condition = build_join_condition(
            gene_family_1.id, gene_1.gene_family_id, filter_column=gene_family_1.name, filter_list=gene_family)
        query = query.join(gene_family_1, and_(
            *gene_family_join_condition), isouter=is_outer)

    if 'geneFunction' in requested or gene_function:
        is_outer = not bool(gene_function)
        gene_function_join_condition = build_join_condition(
            gene_function_1.id, gene_1.gene_function_id, filter_column=gene_function_1.name, filter_list=gene_function)
        query = query.join(gene_function_1, and_(
            *gene_function_join_condition), isouter=is_outer)

    if 'immuneCheckpoint' in requested or immune_checkpoint:
        is_outer = not bool(immune_checkpoint)
        immune_checkpoint_join_condition = build_join_condition(
            immune_checkpoint_1.id, gene_1.immune_checkpoint_id, filter_column=immune_checkpoint_1.name, filter_list=immune_checkpoint)
        query = query.join(immune_checkpoint_1, and_(
            *immune_checkpoint_join_condition), isouter=is_outer)

    if 'pathway' in requested or pathway:
        is_outer = not bool(pathway)
        pathway_join_condition = build_join_condition(
            pathway_1.id, gene_1.pathway_id, filter_column=pathway_1.name, filter_list=pathway)
        query = query.join(pathway_1, and_(
            *pathway_join_condition), isouter=is_outer)

    if 'superCategory' in requested or super_category:
        is_outer = not bool(super_category)
        super_category_join_condition = build_join_condition(
            super_category_1.id, gene_1.super_cat_id, filter_column=super_category_1.name, filter_list=super_category)
        query = query.join(super_category_1, and_(
            *super_category_join_condition), isouter=is_outer)

    if 'therapyType' in requested or therapy_type:
        is_outer = not bool(therapy_type)
        therapy_type_join_condition = build_join_condition(
            therapy_type_1.id, gene_1.therapy_type_id, filter_column=therapy_type_1.name, filter_list=therapy_type)
        query = query.join(therapy_type_1, and_(
            *therapy_type_join_condition), isouter=is_outer)

    if tag_requested or tag or sample or data_set or related or max_rna_seq_expr != None or min_rna_seq_expr != None or 'rnaSeqExprs' in requested or 'samples' in requested:
        gene_sample_join_condition = [gene_to_sample_1.gene_id == gene_1.id]
        if max_rna_seq_expr != None:
            gene_sample_join_condition.append(
                gene_to_sample_1.rna_seq_expr <= max_rna_seq_expr)
        if min_rna_seq_expr != None:
            gene_sample_join_condition.append(
                gene_to_sample_1.rna_seq_expr >= min_rna_seq_expr)
        query = query.join(gene_to_sample_1, and_(*gene_sample_join_condition))

        if 'samples' in requested or sample:
            sample_join_condition = build_join_condition(
                sample_1.id, gene_to_sample_1.sample_id, sample_1.name, sample)
            query = query.join(sample_1, and_(*sample_join_condition))

        if data_set or related:
            data_set_1 = aliased(Dataset, name='d')
            data_set_to_sample_1 = aliased(DatasetToSample, name='dts')

            data_set_sub_query = sess.query(data_set_1.id).filter(
                data_set_1.name.in_(data_set)) if data_set else data_set

            data_set_to_sample_join_condition = build_join_condition(
                data_set_to_sample_1.sample_id, gene_to_sample_1.sample_id, data_set_to_sample_1.dataset_id, data_set_sub_query)
            query = query.join(
                data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

        if tag_requested or tag:
            sample_to_tag_1 = aliased(SampleToTag, name='stt')
            sample_to_tag_join_condition = [
                sample_to_tag_1.sample_id == gene_to_sample_1.sample_id]

        if related:
            data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
            related_tag_1 = aliased(Tag, name='rt')

            related_tag_sub_query = sess.query(related_tag_1.id).filter(
                related_tag_1.name.in_(related))

            data_set_tag_join_condition = build_join_condition(
                data_set_to_tag_1.dataset_id, data_set_to_sample_1.dataset_id, data_set_to_tag_1.tag_id, related_tag_sub_query)
            query = query.join(
                data_set_to_tag_1, and_(*data_set_tag_join_condition))

            if tag_requested or tag:
                tag_to_tag_1 = aliased(TagToTag, name='tt')
                tag_to_tag_subquery = sess.query(tag_to_tag_1.tag_id).filter(
                    tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id)

                sample_to_tag_join_condition.append(
                    sample_to_tag_1.tag_id.in_(tag_to_tag_subquery))

        if tag_requested or tag:
            query = query.join(sample_to_tag_1, and_(
                *sample_to_tag_join_condition))
            tag_join_condition = build_join_condition(
                tag_1.id, sample_to_tag_1.tag_id, tag_1.name, tag)
            query = query.join(tag_1, and_(*tag_join_condition))

    # Only group if array_aggr is used in the selection.
    if 'samples' in requested or 'rnaSeqExprs' in requested:
        group = [gene_1.id]
        append_to_group = group.append
        if 'tag' in tag_requested:
            append_to_group(tag_1.name)
        if 'shortDisplay' in tag_requested:
            append_to_group(tag_1.short_display)
        if 'longDisplay' in tag_requested:
            append_to_group(tag_1.long_display)
        if 'color' in tag_requested:
            append_to_group(tag_1.color)
        if 'characteristics' in tag_requested:
            append_to_group(tag_1.characteristics)
        if 'entrez' in requested:
            append_to_group(gene_1.entrez)
        if 'hgnc' in requested:
            append_to_group(gene_1.hgnc)
        if 'geneFamily' in requested:
            append_to_group(gene_family_1.name)
        if 'friendlyName' in requested:
            append_to_group(gene_1.friendly_name)
        if 'ioLandscapeName' in requested:
            append_to_group(gene_1.io_landscape_name)
        if 'geneFunction' in requested:
            append_to_group(gene_function_1.name)
        if 'superCategory' in requested:
            append_to_group(super_category_1.name)
        if 'therapyType' in requested:
            append_to_group(therapy_type_1.name)
        if 'immuneCheckpoint' in requested:
            append_to_group(immune_checkpoint_1.name)
        if 'pathway' in requested:
            append_to_group(pathway_1.name)
        if 'description' in requested:
            append_to_group(gene_1.description)
        query = query.group_by(*group)

    # return get_pagination_queries(query, paging, distinct, cursor_field=copy_number_result_1.id)
    order = []
    append_to_order = order.append
    if 'tag' in tag_requested:
        append_to_order(tag_1.name)
    if 'shortDisplay' in tag_requested:
        append_to_order(tag_1.short_display)
    if 'longDisplay' in tag_requested:
        append_to_order(tag_1.long_display)
    if 'color' in tag_requested:
        append_to_order(tag_1.color)
    if 'characteristics' in tag_requested:
        append_to_order(tag_1.characteristics)
    if 'entrez' in requested:
        append_to_order(gene_1.entrez)
    if 'hgnc' in requested:
        append_to_order(gene_1.hgnc)
    if 'geneFamily' in requested:
        append_to_order(gene_family_1.name)
    if 'friendlyName' in requested:
        append_to_order(gene_1.friendly_name)
    if 'ioLandscapeName' in requested:
        append_to_order(gene_1.io_landscape_name)
    if 'geneFunction' in requested:
        append_to_order(gene_function_1.name)
    if 'superCategory' in requested:
        append_to_order(super_category_1.name)
    if 'therapyType' in requested:
        append_to_order(therapy_type_1.name)
    if 'immuneCheckpoint' in requested:
        append_to_order(immune_checkpoint_1.name)
    if 'description' in requested:
        append_to_order(gene_1.description)
    if not order:
        append_to_order(gene_1.id)

    return get_pagination_queries(query, paging, distinct, cursor_field=gene_1.id)


def get_gene_types(
        gene_types_requested, distinct, paging, data_set=None, entrez=None, gene_family=None, gene_function=None, gene_type=None, immune_checkpoint=None, max_rna_seq_expr=None, min_rna_seq_expr=None, pathway=None, related=None, sample=None, super_category=None, tag=None, therapy_type=None, gene_ids=[]):
    sess = db.session

    gene_type_1 = aliased(GeneType, name='gt')
    gene_to_gene_type_1 = aliased(GeneToType, name='ggt')

    core_field_mapping = {'name': gene_type_1.name.label('name'),
                          'display': gene_type_1.display.label('display')}

    core = get_selected(gene_types_requested, core_field_mapping)
    # Always select the sample id and the gene id.
    core |= {gene_type_1.id.label('id'),
             gene_to_gene_type_1.gene_id.label('gene_id')}

    gene_type_query = sess.query(*core)
    gene_type_query = gene_type_query.select_from(gene_type_1)

    if not gene_ids:
        query, _count_query = build_gene_request(
            set(), set(), distinct=distinct, paging=paging, data_set=data_set, entrez=entrez, gene_family=gene_family, gene_function=gene_function, gene_type=gene_type, immune_checkpoint=immune_checkpoint, max_rna_seq_expr=max_rna_seq_expr, min_rna_seq_expr=min_rna_seq_expr, pathway=pathway, related=related, sample=sample, super_category=super_category, tag=tag, therapy_type=therapy_type)
        res = fetch_page(query, paging, distinct)
        genes = list(set(gene.id for gene in res)) if len(res) > 0 else []
    else:
        genes = gene_ids

    gene_gene_type_join_condition = build_join_condition(
        gene_to_gene_type_1.type_id, gene_type_1.id, gene_to_gene_type_1.gene_id, genes)

    if gene_type:
        gene_gene_type_join_condition.append(
            gene_type_1.name.in_(gene_type))

    gene_type_query = gene_type_query.join(gene_to_gene_type_1, and_(
        *gene_gene_type_join_condition))

    order = []
    append_to_order = order.append
    if 'name' in gene_types_requested:
        append_to_order(gene_type_1.name)
    if 'display' in gene_types_requested:
        append_to_order(gene_type_1.display)
    if not order:
        append_to_order(gene_type_1.id)
    gene_type_query = gene_type_query.order_by(*order)

    return gene_type_query.distinct().all()


def get_publications(
        publications_requested, distinct, paging, data_set=None, entrez=None, gene_family=None, gene_function=None, gene_type=[], immune_checkpoint=None, max_rna_seq_expr=None, min_rna_seq_expr=None, pathway=None, related=None, sample=None, super_category=None, tag=None, therapy_type=None, gene_ids=[]):

    sess = db.session

    gene_1 = aliased(Gene, name='g')
    pub_1 = aliased(Publication, name='p')
    pub_gene_gene_type_1 = aliased(
        PublicationToGeneToGeneType, name='pggt')

    core_field_mapping = {'doId': pub_1.do_id.label('do_id'),
                          'firstAuthorLastName': pub_1.first_author_last_name.label('first_author_last_name'),
                          'journal': pub_1.journal.label('journal'),
                          'name': pub_1.name.label('name'),
                          'pubmedId': pub_1.pubmed_id.label('pubmed_id'),
                          'title': pub_1.title.label('title'),
                          'year': pub_1.year.label('year')}

    core = get_selected(publications_requested, core_field_mapping)
    # Always select the sample id and the gene id.
    core.add(pub_gene_gene_type_1.gene_id.label('gene_id'))

    pub_query = sess.query(*core)
    pub_query = pub_query.select_from(pub_1)

    gene_subquery, _count_query = build_gene_request(set(), set(), distinct=distinct, paging=paging, data_set=data_set, entrez=entrez, gene_family=gene_family, gene_function=gene_function, gene_type=gene_type,
                                                     immune_checkpoint=immune_checkpoint, max_rna_seq_expr=max_rna_seq_expr, min_rna_seq_expr=min_rna_seq_expr, pathway=pathway, related=related, sample=sample, super_category=super_category, tag=tag, therapy_type=therapy_type)

    pub_gene_gene_type_join_condition = build_pub_gene_gene_type_join_condition(
        gene_subquery, gene_type, pub_gene_gene_type_1, pub_1)
    pub_query = pub_query.join(pub_gene_gene_type_1, and_(
        *pub_gene_gene_type_join_condition))

    order = []
    append_to_order = order.append
    if 'name' in publications_requested:
        append_to_order(pub_1.name)
    if 'pubmedId' in publications_requested:
        append_to_order(pub_1.pubmed_id)
    if 'doId' in publications_requested:
        append_to_order(pub_1.do_id)
    if 'title' in publications_requested:
        append_to_order(pub_1.title)
    if 'firstAuthorLastName' in publications_requested:
        append_to_order(pub_1.first_author_last_name)
    if 'year' in publications_requested:
        append_to_order(pub_1.year)
    if 'journal' in publications_requested:
        append_to_order(pub_1.journal)
    pub_query = pub_query.order_by(*order) if order else pub_query

    return pub_query.distinct().all()


def request_gene(requested, **kwargs):
    '''
    Keyword arguments are:
        `entrez` - a list of integers
        `sample` - a list of strings
    '''
    query = build_gene_request(requested, set(), **kwargs)
    return query.one_or_none()


def request_genes(*args, **kwargs):
    '''
    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'tag' node of the graphql request. If 'tag' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `entrez` - a list of integers, gene entrez ids
        `feature` - a list of strings, feature names
        `feature_class` - a list of strings, feature class names
        `gene_family` - a list of strings, gene family names
        `gene_function` - a list of strings, gene function names
        `gene_type` - a list of strings, gene type names
        `immune_checkpoint` - a list of strings, immune checkpoint names
        `max_rna_seq_expr` - a float, a maximum RNA Sequence Expression value
        `min_rna_seq_expr` - a float, a minimum RNA Sequence Expression value
        `pathway` - a list of strings, pathway names
        'paging' - an instance of PagingInput
            `type` - a string, the type of pagination to perform. Must be either 'OFFSET' or 'CURSOR'."
            `page` - an integer, when performing OFFSET paging, the page number requested.
            `limit` - an integer, when performing OFFSET paging, the number or records requested.
            `first` - an integer, when performing CURSOR paging, the number of records requested AFTER the CURSOR.
            `last` - an integer, when performing CURSOR paging, the number of records requested BEFORE the CURSOR.
            `before` - an integer, when performing CURSOR paging: the CURSOR to be used in tandem with 'last'
            `after` - an integer, when performing CURSOR paging: the CURSOR to be used in tandem with 'first'
        `related` - a list of strings, tag names related to data sets
        `sample` - a list of strings, sample names
        `super_category` - a list of strings, super category names
        `tag` - a list of strings, tag names related to samples
        `therapy_type` - a list of strings, therapy type names
        `distinct` - a boolean, true if the results should have DISTINCT applied
    '''
    distinct = kwargs.pop('distinct', False)
    genes_query = build_gene_request(*args, **kwargs)

    if distinct:
        genes_query = genes_query.distinct()

    return genes_query.all()


def return_gene_derived_fields(requested, gene_types_requested, publications_requested, distinct, paging, **kwargs):
    '''
    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `entrez` - a list of integers, gene entrez ids
        `feature` - a list of strings, feature names
        `feature_class` - a list of strings, feature class names
        `gene_family` - a list of strings, gene family names
        `gene_function` - a list of strings, gene function names
        `gene_type` - a list of strings, gene type names
        `immune_checkpoint` - a list of strings, immune checkpoint names
        `max_rna_seq_expr` - a float, a maximum RNA Sequence Expression value
        `min_rna_seq_expr` - a float, a minimum RNA Sequence Expression value
        `pathway` - a list of strings, pathway names
        `related` - a list of strings, tag names related to data sets
        `sample` - a list of strings, sample names
        `super_category` - a list of strings, super category names
        `tag` - a list of strings, tag names related to samples
        `therapy_type` - a list of strings, therapy type names
        `gene_ids` - a list of integers, gene ids already retrieved from the database
    '''
    gene_types = get_gene_types(gene_types_requested, distinct=distinct,
                                paging=paging, **kwargs) if 'geneTypes' in requested else []

    pubs = get_publications(publications_requested, distinct=distinct,
                            paging=paging, **kwargs) if 'publications' in requested else []

    types_dict = dict()
    for key, collection in groupby(gene_types, key=lambda gt: gt.gene_id):
        types_dict[key] = types_dict.get(key, []) + list(collection)

    pubs_dict = dict()
    for key, collection in groupby(pubs, key=lambda pub: pub.gene_id):
        pubs_dict[key] = pubs_dict.get(key, []) + list(collection)

    return (pubs_dict, types_dict)
