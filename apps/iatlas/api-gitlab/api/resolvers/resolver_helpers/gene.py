from sqlalchemy import and_, orm
from sqlalchemy.orm.attributes import set_committed_value
from itertools import chain, groupby
from api import db
from api.database import return_gene_query
from api.db_models import (
    Dataset, DatasetToSample, Gene, GeneFamily, GeneFunction, GeneToSample, GeneToType,
    GeneType, ImmuneCheckpoint, Pathway, Publication, PublicationToGeneToGeneType,
    SuperCategory, Sample, SampleToTag, Tag, TagToTag, TherapyType)
from .general_resolvers import build_join_condition, build_option_args, get_selection_set, get_value
from .tag import request_tags


def build_core_field_mapping(model):
    return {'entrez': model.entrez.label('entrez'),
            'hgnc': model.hgnc.label('hgnc'),
            'description': model.description.label('description'),
            'friendlyName': model.friendly_name.label('friendly_name'),
            'ioLandscapeName': model.io_landscape_name.label('io_landscape_name')}


def build_gene_type_id_map(gene):
    if gene.gene_types:
        return map(lambda gene_type: gene_type.id, gene.gene_types)


def build_pub_gene_gene_type_join_condition(genes, pub_gene_gene_type_model, pub_model):
    pub_gene_gene_type_join_condition = [pub_gene_gene_type_model.publication_id == pub_model.id, pub_gene_gene_type_model.gene_id.in_(
        [gene.id for gene in genes])]

    map_of_ids = list(map(build_gene_type_id_map, genes))
    chain_of_ids = chain.from_iterable(map_of_ids) if any(map_of_ids) else None
    gene_type_ids = set(chain_of_ids) if chain_of_ids else None

    if gene_type_ids:
        pub_gene_gene_type_join_condition.append(
            pub_gene_gene_type_model.gene_type_id.in_(gene_type_ids))

    return pub_gene_gene_type_join_condition


def build_gene_core_request(selection_set, entrez=None):
    """
    Builds a SQL request with just core gene fields.
    """
    sess = db.session

    gene_1 = orm.aliased(Gene, name='g')

    core_field_mapping = build_core_field_mapping(gene_1)

    core = build_option_args(selection_set, core_field_mapping)

    # Need at least one column to select.
    if not core:
        core.append(gene_1.id)
    query = sess.query(*core)

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    return query


def build_gene_request(_obj, info, gene_type=None, entrez=None, samples=None, by_tag=False):
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
    gene_type_1 = orm.aliased(GeneType, name='gt')
    immune_checkpoint_1 = orm.aliased(ImmuneCheckpoint, name='ic')
    pathway_1 = orm.aliased(Pathway, name='py')
    pub_1 = orm.aliased(Publication, name='p')
    super_category_1 = orm.aliased(SuperCategory, name='sc')
    tag_1 = orm.aliased(Tag, name='t')
    therapy_type_1 = orm.aliased(TherapyType, name='tht')

    related_field_mapping = {'geneFamily': 'gene_family',
                             'geneFunction': 'gene_function',
                             'geneTypes': 'gene_types',
                             'immuneCheckpoint': 'immune_checkpoint',
                             'pathway': 'pathway',
                             'publications': 'publications',
                             'rnaSeqExpr': 'rna_seq_expr',
                             'superCategory': 'super_category',
                             'therapyType': 'therapy_type'}

    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []
    append_to_option_args = option_args.append

    query = sess.query(gene_1)

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    if 'gene_family' in relations:
        append_to_option_args(orm.subqueryload(
            gene_1.gene_family.of_type(gene_family_1)))

    if 'gene_function' in relations:
        append_to_option_args(orm.subqueryload(
            gene_1.gene_function.of_type(gene_function_1)))

    if 'gene_types' in relations or gene_type:
        query = query.join(gene_type_1, gene_1.gene_types)
        if gene_type:
            query = query.filter(gene_type_1.name.in_(gene_type))

        append_to_option_args(orm.contains_eager(
            gene_1.gene_types.of_type(gene_type_1)))

    if 'immune_checkpoint' in relations:
        append_to_option_args(orm.subqueryload(
            gene_1.immune_checkpoint.of_type(immune_checkpoint_1)))

    if 'pathway' in relations:
        append_to_option_args(orm.subqueryload(
            gene_1.pathway.of_type(pathway_1)))

    if samples or 'rna_seq_expr' in relations:
        append_to_option_args(orm.subqueryload(
            gene_1.gene_sample_assoc.of_type(gene_to_sample_1)))
        if samples:
            sample_1 = orm.aliased(Sample, name='s')
            query = query.filter(gene_to_sample_1.sample_id.in_(sess.query(sample_1.id).filter(
                sample_1.name.in_(samples))))

    if 'super_category' in relations:
        append_to_option_args(orm.subqueryload(
            gene_1.super_category.of_type(super_category_1)))

    if 'therapy_type' in relations:
        append_to_option_args(orm.subqueryload(
            gene_1.therapy_type.of_type(therapy_type_1)))

    if option_args:
        query = query.options(*option_args)
        if gene_type:
            query = query.filter(gene_type_1.name.in_(gene_type))
        return query
    elif 'publications' in relations:
        return query

    return build_gene_core_request(selection_set, entrez)


def get_rna_seq_expr(gene_row):
    def build_array(gene_sample_rel):
        rna_seq_expr_value = get_value(
            gene_sample_rel, 'rna_seq_expr')
        if rna_seq_expr_value:
            return rna_seq_expr_value
    return map(build_array, get_value(gene_row, 'gene_sample_assoc', []))


def request_gene(_obj, info, entrez=None):
    query = build_gene_request(_obj, info, entrez=[entrez])
    gene = query.one_or_none()

    if gene:
        get_publications(info, [gene])

    return gene


def request_genes(_obj, info, entrez=None, gene_type=None, samples=None, by_tag=False):
    genes_query = build_gene_request(_obj, info, entrez=entrez, gene_type=gene_type,
                                     samples=samples, by_tag=by_tag)
    genes = genes_query.distinct().all()

    get_publications(info, genes, by_tag)

    return genes


def get_publications(info, genes, by_tag=False):
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
        pub_select_fields = [pub_1.do_id.label('do_id'),
                             pub_1.first_author_last_name.label(
                                 'first_author_last_name'),
                             pub_1.journal.label('journal'),
                             pub_1.name.label('name'),
                             pub_1.pubmed_id.label('pubmed_id'),
                             pub_1.title.label('title'),
                             pub_1.year.label('year')]

        pub_core = build_option_args(
            pub_selection_set, pub_core_field_mapping) or pub_select_fields

        pub_query = sess.query(
            *pub_core, pub_gene_gene_type_1.gene_id.label('gene_id'))
        pub_query = pub_query.select_from(pub_1)

        pub_gene_gene_type_join_condition = build_pub_gene_gene_type_join_condition(
            genes, pub_gene_gene_type_1, pub_1)
        pub_query = pub_query.join(pub_gene_gene_type_1, and_(
            *pub_gene_gene_type_join_condition))

        publications = pub_query.distinct().all()

        gene_dict = {gene.id: gene for gene in genes}

        for key, collection in groupby(publications, key=lambda publication: publication.gene_id):
            set_committed_value(
                gene_dict[key], 'publications', list(collection))
