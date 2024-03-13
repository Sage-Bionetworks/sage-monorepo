from sqlalchemy import and_
from sqlalchemy.orm import aliased
from itertools import groupby
from api import db
from api.db_models import (
    Cohort,
    CohortToSample,
    CohortToGene,
    Gene,
    GeneToSample,
    GeneToGeneSet,
    GeneSet,
    Publication,
    PublicationToGeneToGeneSet,
    Sample,
    SingleCellPseudobulk
)
from .general_resolvers import build_join_condition, get_selected, get_value
from .publication import build_publication_graphql_response
from .paging_utils import get_pagination_queries, fetch_page
from .sample import build_sample_graphql_response, build_gene_expression_graphql_response, build_single_cell_seq_response


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
    'cellTypeSamples',
    'superCategory',
    'therapyType'
})


def get_simple_gene_column_labels(requested, gene):
    mapping = {
        'entrez': gene.entrez_id.label('gene_entrez'),
        'hgnc': gene.hgnc_id.label('gene_hgnc'),
        'description': gene.description.label('gene_description'),
        'friendlyName': gene.friendly_name.label('gene_friendly_name'),
        'ioLandscapeName': gene.io_landscape_name.label('gene_io_landscape_name')
    }
    labels = get_selected(requested, mapping)
    return(labels)


def build_gene_graphql_response(
    requested=[],
    gene_types_requested=[],
    publications_requested=[],
    sample_requested=[],
    cell_type_sample_requested=[],
    gene_type=None,
    cohort=None,
    sample=None,
    max_rna_seq_expr=None,
    min_rna_seq_expr=None,
    prefix='gene_'
):

    def f(gene):
        if not gene:
            return None

        id = get_value(gene, prefix + 'id')
        gene_types = get_gene_types(
            id, requested, gene_types_requested, gene_type=gene_type)
        publications = get_publications(id, requested, publications_requested)
        samples = get_samples(id, requested, sample_requested,
                              cohort, sample, max_rna_seq_expr, min_rna_seq_expr)
        cell_type_samples = get_cell_type_samples(
            id, requested=requested, cell_type_sample_requested=cell_type_sample_requested, cohort=cohort, sample=sample
        )
        result_dict = {
            'id': id,
            'entrez': get_value(gene, prefix + 'entrez') or get_value(gene, prefix + 'entrez_id'),
            'hgnc': get_value(gene, prefix + 'hgnc') or get_value(gene, prefix + 'hgnc_id'),
            'description': get_value(gene, prefix + 'description'),
            'friendlyName': get_value(gene, prefix + 'friendly_name'),
            'ioLandscapeName': get_value(gene, prefix + 'io_landscape_name'),
            'geneFamily': get_value(gene, prefix + 'family'),
            'geneFunction': get_value(gene, prefix + 'function'),
            'immuneCheckpoint': get_value(gene, prefix + 'immune_checkpoint'),
            'pathway': get_value(gene, prefix + 'pathway'),
            'superCategory': get_value(gene, prefix + 'super_category'),
            'therapyType': get_value(gene, prefix + 'therapy_type'),
            'geneTypes': gene_types,
            'publications': map(build_publication_graphql_response, publications)
        }
        result_dict['samples'] = map(build_gene_expression_graphql_response(), samples)
        result_dict['cellTypeSamples'] = map(build_single_cell_seq_response(), cell_type_samples)
        return result_dict
    return f


def build_pub_gene_gene_type_join_condition(gene_ids, gene_type, pub_gene_gene_type_model, pub_model):
    join_condition = build_join_condition(
        pub_gene_gene_type_model.publication_id, pub_model.id, pub_gene_gene_type_model.gene_id, gene_ids)

    if gene_type:
        gene_type_1 = aliased(GeneSet, name='gt')
        gene_type_subquery = db.session.query(gene_type_1.id).filter(
            gene_type_1.name.in_(gene_type))
        join_condition.append(
            pub_gene_gene_type_model.gene_type_id.in_(gene_type_subquery))

    return join_condition


def build_gene_request(
    requested,
    distinct=False,
    paging=None,
    entrez=None,
    gene_family=None,
    gene_function=None,
    gene_type=None,
    immune_checkpoint=None,
    pathway=None,
    super_category=None,
    therapy_type=None,
    cohort=None,
    sample=None,
    cell_type_sample=None,
    max_rna_seq_expr=None,
    min_rna_seq_expr=None
):
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
    gene_to_sample_1 = aliased(GeneToSample, name='gts')
    gene_to_type_1 = aliased(GeneToGeneSet, name='ggt')
    gene_type_1 = aliased(GeneSet, name='gt')
    sample_1 = aliased(Sample, name='s')
    cohort_1 = aliased(Cohort, name='c')
    cohort_to_gene_1 = aliased(CohortToGene, name='ctg')
    pseudobulk_1 = aliased(SingleCellPseudobulk, name='scp')
    cohort_to_sample_1 = aliased(CohortToSample, name='cts')

    core_field_mapping = {
        'id': gene_1.id.label('gene_id'),
        'entrez': gene_1.entrez_id.label('gene_entrez'),
        'hgnc': gene_1.hgnc_id.label('gene_hgnc'),
        'description': gene_1.description.label('gene_description'),
        'friendlyName': gene_1.friendly_name.label('gene_friendly_name'),
        'ioLandscapeName': gene_1.io_landscape_name.label('gene_io_landscape_name'),
        'geneFamily': gene_1.gene_family.label('gene_family'),
        'geneFunction': gene_1.gene_function.label('gene_function'),
        'immuneCheckpoint': gene_1.immune_checkpoint.label('gene_immune_checkpoint'),
        'pathway': gene_1.gene_pathway.label('gene_pathway'),
        'superCategory': gene_1.super_category.label('gene_super_category'),
        'therapyType': gene_1.therapy_type.label('gene_therapy_type')
    }

    core = get_selected(requested, core_field_mapping)
    core |= {gene_1.id.label('gene_id')}

    query = sess.query(*core)
    query = query.select_from(gene_1)

    if entrez:
        query = query.filter(gene_1.entrez_id.in_(entrez))

    if gene_type:
        query = query.join(
            gene_to_type_1, and_(
                gene_to_type_1.gene_id == gene_1.id, gene_to_type_1.gene_set_id.in_(
                    sess.query(gene_type_1.id).filter(
                        gene_type_1.name.in_(gene_type))
                )
            )
        )

    if gene_family:
        query = query.filter(gene_1.gene_family.in_(gene_family))

    if gene_function:
        query = query.filter(gene_1.gene_function.in_(gene_function))

    if immune_checkpoint:
        query = query.filter(gene_1.immune_checkpoint.in_(immune_checkpoint))

    if pathway:
        query = query.filter(gene_1.pathway.in_(pathway))

    if super_category:
        query = query.filter(gene_1.super_category.in_(super_category))

    if therapy_type:
        query = query.filter(gene_1.therapy_type.in_(therapy_type))

    if max_rna_seq_expr or min_rna_seq_expr or sample:
        gene_to_sample_subquery = sess.query(gene_to_sample_1.gene_id)

        if max_rna_seq_expr:
            gene_to_sample_subquery = gene_to_sample_subquery.filter(
                gene_to_sample_1.rna_seq_expression <= max_rna_seq_expr)

        if min_rna_seq_expr:
            gene_to_sample_subquery = gene_to_sample_subquery.filter(
                gene_to_sample_1.rna_seq_expression >= min_rna_seq_expr)

        if sample:

            sample_join_condition = build_join_condition(
                gene_to_sample_1.sample_id, sample_1.id, filter_column=sample_1.name, filter_list=sample)
            gene_to_sample_subquery = gene_to_sample_subquery.join(sample_1, and_(
                *sample_join_condition), isouter=False)

            gene_to_sample_subquery = gene_to_sample_subquery.filter(
                sample_1.name.in_(sample))

        query = query.filter(gene_1.id.in_(gene_to_sample_subquery))


    if cell_type_sample:

        cell_type_sample_subquery = sess.query(pseudobulk_1.feature_id)


        sample_join_condition = build_join_condition(
            pseudobulk_1.sample_id, sample_1.id, filter_column=sample_1.name, filter_list=cell_type_sample
        )
        cell_type_sample_subquery = cell_type_sample_subquery.join(sample_1, and_(
            *sample_join_condition), isouter=False)

        query = query.filter(gene_1.id.in_(cell_type_sample_subquery))

    if cohort:

        if cell_type_sample or "cellTypeSamples" in requested:

            cohort_subquery = sess.query(pseudobulk_1.gene_id)

            cohort_to_sample_join_condition = build_join_condition(
                pseudobulk_1.sample_id, cohort_to_sample_1.sample_id
            )
            cohort_subquery = cohort_subquery.join(cohort_to_sample_1,and_(
                *cohort_to_sample_join_condition), isouter=False
            )

            cohort_join_condition = build_join_condition(
                cohort_to_sample_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort
            )
            cohort_subquery = cohort_subquery.join(cohort_1,and_(
                *cohort_join_condition), isouter=False
            )

        else:

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
        'rnaSeqExpr': gene_to_sample_1.rna_seq_expression.label('sample_gene_rna_seq_expr'),
        'nanostringExpr': gene_to_sample_1.nanostring_expression.label('sample_gene_nanostring_expr')
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
            gene_to_sample_1.rna_seq_expression <= max_rna_seq_expr)

    if min_rna_seq_expr:
        query = query.filter(
            gene_to_sample_1.rna_seq_expression >= min_rna_seq_expr)

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

def get_cell_type_samples(gene_id, requested, cell_type_sample_requested, cohort=None, sample=None):


    if 'cellTypeSamples' not in requested:
        return []

    sess = db.session

    sample_1 = aliased(Sample, name='s')
    cohort_1 = aliased(Cohort, name='c')
    cohort_to_sample_1 = aliased(CohortToSample, name='cts')
    pseudobulk_1 = aliased(SingleCellPseudobulk, name='scp')

    sample_core_field_mapping = {
        'name': sample_1.name.label('sample_name')}

    sample_core = get_selected(cell_type_sample_requested, sample_core_field_mapping)

    sample_core |= {
        sample_1.id.label('sample_id'),
        pseudobulk_1.gene_id.label('sample_gene_id')
    }

    if 'singleCellSeqSum' in cell_type_sample_requested:
        sample_core |= {
            pseudobulk_1.single_cell_seq_sum.label('sample_single_cell_seq_sum')
        }

    if 'cellType' in cell_type_sample_requested:
        sample_core |= {
            pseudobulk_1.cell_type.label('sample_cell_type')
        }

    query = sess.query(*sample_core)
    query = query.select_from(sample_1)

    query = query.filter(pseudobulk_1.gene_id.in_([gene_id]))

    sample_join_condition = build_join_condition(
        pseudobulk_1.sample_id,
        sample_1.id,
    )
    query = query.join(
        pseudobulk_1, and_(*sample_join_condition))

    if sample:
        query = query.filter(sample_1.name.in_(sample))

    if cohort:
        cohort_subquery = sess.query(pseudobulk_1.gene_id)

        cohort_to_sample_join_condition = build_join_condition(
            pseudobulk_1.sample_id, cohort_to_sample_1.sample_id
        )
        cohort_subquery = cohort_subquery.join(cohort_to_sample_1,and_(
            *cohort_to_sample_join_condition), isouter=False
        )

        cohort_join_condition = build_join_condition(
            cohort_to_sample_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort
        )
        cohort_subquery = cohort_subquery.join(cohort_1,and_(
            *cohort_join_condition), isouter=False
        )

        query = query.filter(pseudobulk_1.gene_id.in_(cohort_subquery))


    samples = query.distinct().all()
    return samples



def get_gene_types(gene_id, requested, gene_types_requested, gene_type=None):

    if 'geneTypes' not in requested:
        return None

    sess = db.session

    gene_type_1 = aliased(GeneSet, name='gt')
    gene_to_gene_type_1 = aliased(GeneToGeneSet, name='ggt')

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
        gene_to_gene_type_1.gene_set_id, gene_type_1.id, gene_to_gene_type_1.gene_id, [gene_id])

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
    pub_gene_gene_type_1 = aliased(PublicationToGeneToGeneSet, name='pggt')

    core_field_mapping = {
        'doId': pub_1.do_id.label('do_id'),
        'firstAuthorLastName': pub_1.first_author_last_name.label('first_author_last_name'),
        'journal': pub_1.journal.label('journal'),
        'name': pub_1.title.label('name'),
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
