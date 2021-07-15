from sqlalchemy import and_
from sqlalchemy.orm import aliased
from itertools import groupby
from api import db
from api.db_models import Cohort, CohortToSample, CohortToGene, Gene, GeneFamily, GeneFunction, GeneToSample, GeneToType, GeneType, ImmuneCheckpoint, Pathway, Publication, PublicationToGeneToGeneType, SuperCategory, Sample, TherapyType
from .general_resolvers import build_join_condition, get_selected, get_value
from .publication import build_publication_graphql_response
from .paging_utils import get_pagination_queries, fetch_page
from .sample import build_sample_graphql_response


simple_gene_request_fields = {
    'entrez',
    'hgnc',
    'description',
    'friendlyName',
    'ioLandscapeName'
}

gene_request_fields = simple_gene_request_fields.union({
    'geneFamily',
    'geneFunction',
    'geneTypes',
    'immuneCheckpoint',
    'pathway',
    'publications',
    'samples',
    'superCategory',
    'therapyType'
})


def get_simple_gene_column_labels(requested, gene):
    mapping = {
        'entrez': gene.entrez.label('gene_entrez'),
        'hgnc': gene.hgnc.label('gene_hgnc'),
        'description': gene.description.label('gene_description'),
        'friendlyName': gene.friendly_name.label('gene_friendly_name'),
        'ioLandscapeName': gene.io_landscape_name.label('gene_io_landscape_name')
    }
    labels = get_selected(requested, mapping)
    return(labels)


def build_gene_graphql_response(requested=[], gene_types_requested=[], publications_requested=[], sample_requested=[], gene_type=None, cohort=None, sample=None, max_rna_seq_expr=None, min_rna_seq_expr=None, prefix='gene_'):
    def f(gene):
        if not gene:
            return None
        id = get_value(gene, prefix + 'id')
        gene_types = get_gene_types(
            id, requested, gene_types_requested, gene_type=gene_type)
        publications = get_publications(id, requested, publications_requested)
        samples = get_samples(id, requested, sample_requested,
                              cohort, sample, max_rna_seq_expr, min_rna_seq_expr)
        return {
            'id': id,
            'entrez': get_value(gene, prefix + 'entrez'),
            'hgnc': get_value(gene, prefix + 'hgnc'),
            'description': get_value(gene, prefix + 'description'),
            'friendlyName': get_value(gene, prefix + 'friendly_name'),
            'ioLandscapeName': get_value(gene, prefix + 'io_landscape_name'),
            'geneFamily': get_value(gene, prefix + 'family'),
            'geneFunction': get_value(gene, prefix + 'function'),
            'immuneCheckpoint': get_value(gene, 'gene_immune_checkpoint'),
            'pathway': get_value(gene, prefix + 'gene_pathway'),
            'superCategory': get_value(gene, prefix + 'gene_super_category'),
            'therapyType': get_value(gene, prefix + 'gene_therapy_type'),
            'geneTypes': gene_types,
            'publications': map(build_publication_graphql_response, publications),
            'samples': map(build_sample_graphql_response(), samples)
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


def build_gene_request(requested, distinct=False, paging=None, entrez=None, gene_family=None, gene_function=None, gene_type=None, immune_checkpoint=None, pathway=None, super_category=None, therapy_type=None, cohort=None, sample=None, max_rna_seq_expr=None, min_rna_seq_expr=None):
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
    gene_to_sample_1 = aliased(GeneToSample, name='gts')
    gene_to_type_1 = aliased(GeneToType, name='ggt')
    gene_type_1 = aliased(GeneType, name='gt')
    immune_checkpoint_1 = aliased(ImmuneCheckpoint, name='ic')
    pathway_1 = aliased(Pathway, name='py')
    sample_1 = aliased(Sample, name='s')
    super_category_1 = aliased(SuperCategory, name='sc')
    therapy_type_1 = aliased(TherapyType, name='tht')
    cohort_1 = aliased(Cohort, name='c')
    cohort_to_gene_1 = aliased(CohortToGene, name='ctg')

    core_field_mapping = {
        'id': gene_1.id.label('gene_id'),
        'entrez': gene_1.entrez.label('gene_entrez'),
        'hgnc': gene_1.hgnc.label('gene_hgnc'),
        'description': gene_1.description.label('gene_description'),
        'friendlyName': gene_1.friendly_name.label('gene_friendly_name'),
        'ioLandscapeName': gene_1.io_landscape_name.label('gene_io_landscape_name'),
        'geneFamily': gene_family_1.name.label('gene_family'),
        'geneFunction': gene_function_1.name.label('gene_function'),
        'immuneCheckpoint': immune_checkpoint_1.name.label('gene_immune_checkpoint'),
        'pathway': pathway_1.name.label('gene_pathway'),
        'superCategory': super_category_1.name.label('gene_super_category'),
        'therapyType': therapy_type_1.name.label('gene_therapy_type')
    }

    core = get_selected(requested, core_field_mapping)
    core |= {gene_1.id.label('gene_id')}

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

    if max_rna_seq_expr or min_rna_seq_expr or sample:
        gene_to_sample_subquery = sess.query(gene_to_sample_1.gene_id)

        if max_rna_seq_expr:
            gene_to_sample_subquery = gene_to_sample_subquery.filter(
                gene_to_sample_1.rna_seq_expr <= max_rna_seq_expr)

        if min_rna_seq_expr:
            gene_to_sample_subquery = gene_to_sample_subquery.filter(
                gene_to_sample_1.rna_seq_expr >= min_rna_seq_expr)

        if sample:

            sample_join_condition = build_join_condition(
                gene_to_sample_1.sample_id, sample_1.id, filter_column=sample_1.name, filter_list=sample)
            gene_to_sample_subquery = gene_to_sample_subquery.join(sample_1, and_(
                *sample_join_condition), isouter=False)

            gene_to_sample_subquery = gene_to_sample_subquery.filter(
                sample_1.name.in_(sample))

        query = query.filter(gene_1.id.in_(gene_to_sample_subquery))

    if cohort:
        cohort_subquery = sess.query(cohort_to_gene_1.gene_id)

        cohort_join_condition = build_join_condition(
            cohort_to_gene_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)
        cohort_subquery = cohort_subquery.join(cohort_1, and_(
            *cohort_join_condition), isouter=False)

        query = query.filter(gene_1.id.in_(cohort_subquery))

    return get_pagination_queries(query, paging, distinct, cursor_field=gene_1.id)


def get_samples(id, requested, sample_requested, cohort=None, sample=None, max_rna_seq_expr=None, min_rna_seq_expr=None):

    if 'samples' not in requested:
        return []

    sess = db.session

    gene_to_sample_1 = aliased(GeneToSample, name='fts')
    sample_1 = aliased(Sample, name='s')
    cohort_1 = aliased(Cohort, name='c')
    cohort_to_sample_1 = aliased(CohortToSample, name='cts')

    core_field_mapping = {
        'name': sample_1.name.label('sample_name'),
        'rnaSeqExpr': gene_to_sample_1.rna_seq_expr.label('sample_gene_rna_seq_expr')
    }

    core = get_selected(sample_requested, core_field_mapping)

    core |= {
        sample_1.id.label('sample_id'),
        gene_to_sample_1.gene_id.label('gene_id'),
    }

    query = sess.query(*core)
    query = query.select_from(sample_1)

    if sample:
        query = query.filter(sample_1.name.in_(sample))

    gene_sample_join_condition = build_join_condition(
        gene_to_sample_1.sample_id, sample_1.id, gene_to_sample_1.gene_id, [id])

    if max_rna_seq_expr:
        query = query.filter(
            gene_to_sample_1.rna_seq_expr <= max_rna_seq_expr)

    if min_rna_seq_expr:
        query = query.filter(
            gene_to_sample_1.rna_seq_expr >= min_rna_seq_expr)

    query = query.join(
        gene_to_sample_1, and_(*gene_sample_join_condition))

    if cohort:
        cohort_subquery = sess.query(cohort_to_sample_1.sample_id)

        cohort_join_condition = build_join_condition(
            cohort_to_sample_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)
        cohort_subquery = cohort_subquery.join(cohort_1, and_(
            *cohort_join_condition), isouter=False)

        query = query.filter(
            sample_1.id.in_(cohort_subquery))

    samples = query.distinct().all()
    return samples


def get_gene_types(gene_id, requested, gene_types_requested, gene_type=None):

    if 'geneTypes' not in requested:
        return None

    sess = db.session

    gene_type_1 = aliased(GeneType, name='gt')
    gene_to_gene_type_1 = aliased(GeneToType, name='ggt')

    core_field_mapping = {
        'name': gene_type_1.name.label('name'),
        'display': gene_type_1.display.label('display')
    }

    core = get_selected(gene_types_requested, core_field_mapping)
    core |= {
        gene_type_1.id.label('gene_id'),
        gene_to_gene_type_1.gene_id.label('gene_type_id')
    }

    gene_type_query = sess.query(*core)
    gene_type_query = gene_type_query.select_from(gene_type_1)

    gene_gene_type_join_condition = build_join_condition(
        gene_to_gene_type_1.type_id, gene_type_1.id, gene_to_gene_type_1.gene_id, [gene_id])

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


def get_publications(gene_id, requested, publications_requested):

    if 'publications' not in requested:
        return []

    sess = db.session

    pub_1 = aliased(Publication, name='p')
    pub_gene_gene_type_1 = aliased(PublicationToGeneToGeneType, name='pggt')

    core_field_mapping = {
        'doId': pub_1.do_id.label('do_id'),
        'firstAuthorLastName': pub_1.first_author_last_name.label('first_author_last_name'),
        'journal': pub_1.journal.label('journal'),
        'name': pub_1.name.label('name'),
        'pubmedId': pub_1.pubmed_id.label('pubmed_id'),
        'title': pub_1.title.label('title'),
        'year': pub_1.year.label('year')
    }

    core = get_selected(publications_requested, core_field_mapping)
    core.add(pub_gene_gene_type_1.gene_id.label('gene_id'))

    query = sess.query(*core)
    query = query.select_from(pub_1)

    ptgtgt_join_condition = build_join_condition(
        pub_1.id, pub_gene_gene_type_1.publication_id, filter_column=pub_gene_gene_type_1.gene_id, filter_list=[gene_id])
    query = query.join(pub_gene_gene_type_1, and_(
        *ptgtgt_join_condition), isouter=False)

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
    query = query.order_by(*order) if order else query

    return query.distinct().all()
