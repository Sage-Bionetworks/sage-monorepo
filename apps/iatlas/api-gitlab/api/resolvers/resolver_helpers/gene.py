from sqlalchemy import and_, orm
from itertools import chain
from api import db
from api.db_models import (
    Dataset, DatasetToTag, DatasetToSample, Feature, FeatureClass, FeatureToSample, Gene, GeneFamily,
    GeneFunction, GeneToSample, GeneToType, GeneType, ImmuneCheckpoint, Pathway, Publication,
    PublicationToGeneToGeneType, SuperCategory, Sample, SampleToTag, Tag, TagToTag, TherapyType)
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
    join_condition = [
        pub_gene_gene_type_model.publication_id == pub_model.id, pub_gene_gene_type_model.gene_id.in_([*gene_dict])]

    map_of_ids = list(map(lambda gt: gt.id, gene_types))
    gene_type_ids = list(dict.fromkeys(map_of_ids)) if map_of_ids else None

    if gene_type_ids:
        join_condition.append(
            pub_gene_gene_type_model.gene_type_id.in_(gene_type_ids))
    print('gene_type_ids: ', gene_type_ids)

    return join_condition


def build_tag_join_condition(join_column, column, filter_1_column=None, filter_1_list=None, filter_2_column=None, filter_2_list=None):
    join_condition = [join_column == column]
    if bool(filter_1_list):
        join_condition.append(filter_1_column.in_(filter_1_list))
    if bool(filter_2_list):
        join_condition.append(filter_2_column.in_(filter_2_list))
    return join_condition


def build_gene_request(_obj, info, data_set=None, entrez=None, feature=None, feature_class=None, gene_family=None,
                       gene_function=None, gene_type=None, immune_checkpoint=None, pathway=None, related=None,
                       sample=None, super_category=None, tag=None, therapy_type=None, by_tag=False):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, by_tag, child_node='genes')

    tag_selection_set = info.field_nodes[0].selection_set

    gene_1 = orm.aliased(Gene, name='g')
    gene_family_1 = orm.aliased(GeneFamily, name='gf')
    gene_function_1 = orm.aliased(GeneFunction, name='gfn')
    gene_to_type_1 = orm.aliased(GeneToType, name='ggt')
    gene_type_1 = orm.aliased(GeneType, name='gt')
    immune_checkpoint_1 = orm.aliased(ImmuneCheckpoint, name='ic')
    pathway_1 = orm.aliased(Pathway, name='py')
    sample_1 = orm.aliased(Sample, name='s')
    super_category_1 = orm.aliased(SuperCategory, name='sc')
    tag_1 = orm.aliased(Tag, name='t')
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
    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                              'color': tag_1.color.label('color'),
                              'display': tag_1.display.label('display'),
                              'tag': tag_1.name.label('tag')}
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

    if by_tag:
        core = core + \
            build_option_args(tag_selection_set, tag_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(gene_1)

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    if gene_type:
        query = query.join(gene_to_type_1, and_(
            gene_to_type_1.gene_id == gene_1.id, gene_to_type_1.type_id.in_(sess.query(gene_type_1.id).filter(gene_type_1.name.in_(gene_type)))))

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

    if sample or by_tag:
        gene_to_sample_1 = orm.aliased(GeneToSample, name='gs')

        gene_to_sample_sub_query = sess.query(gene_to_sample_1.sample_id).filter(
            gene_to_sample_1.gene_id == gene_1.id)
        sample_join_condition = [sample_1.id.in_(gene_to_sample_sub_query)]

        if sample:
            sample_join_condition = sample_join_condition + \
                [sample_1.name.in_(sample)]

        query = query.join(sample_1, and_(*sample_join_condition))

        if by_tag:
            data_set_1 = orm.aliased(Dataset, name='d')
            sample_to_tag_1 = orm.aliased(SampleToTag, name='stt')

            if data_set or related:
                data_set_to_sample_1 = orm.aliased(DatasetToSample, name='dts')

                data_set_to_sample_sub_query = sess.query(data_set_to_sample_1.dataset_id).filter(
                    data_set_to_sample_1.sample_id == sample_1.id)
                data_set_join_condition = [
                    data_set_1.id.in_(data_set_to_sample_sub_query), data_set_1.name.in_(data_set)]
                query = query.join(data_set_1, and_(*data_set_join_condition))

            if feature or feature_class:
                feature_1 = orm.aliased(Feature, name='f')
                feature_class_1 = orm.aliased(FeatureClass, name='fc')
                feature_to_sample_1 = orm.aliased(FeatureToSample, name='fs')

                query = query.join(feature_to_sample_1,
                                   feature_to_sample_1.sample_id == sample_1.id)

                feature_join_condition = build_tag_join_condition(
                    feature_1.id, feature_to_sample_1.feature_id, feature_1.name, feature)
                query = query.join(feature_1, and_(*feature_join_condition))

                if feature_class:
                    feature_class_join_condition = build_tag_join_condition(
                        feature_class_1.id, feature_1.class_id, feature_class_1.name, feature_class)
                    query = query.join(
                        feature_class_1, and_(*feature_class_join_condition))

            sample_to_tag_join_condition = [
                sample_to_tag_1.sample_id == sample_1.id]

            if related:
                data_set_to_tag_1 = orm.aliased(DatasetToTag, name='dtt')
                related_tag_1 = orm.aliased(Tag, name='rt')
                tag_to_tag_1 = orm.aliased(TagToTag, name='tt')

                data_set_to_tag_subquery = sess.query(
                    data_set_to_tag_1.tag_id).filter(data_set_to_tag_1.dataset_id == data_set_1.id)
                related_tag_join_condition = [related_tag_1.name.in_(
                    related), related_tag_1.id.in_(data_set_to_tag_subquery)]
                query = query.join(related_tag_1, and_(
                    *related_tag_join_condition))

                tag_to_tag_subquery = sess.query(tag_to_tag_1.tag_id).filter(
                    tag_to_tag_1.related_tag_id == related_tag_1.id)

                sample_to_tag_join_condition.append(
                    sample_to_tag_1.tag_id.in_(tag_to_tag_subquery))

            query = query.join(sample_to_tag_1, and_(
                *sample_to_tag_join_condition))

            tag_join_condition = build_tag_join_condition(
                tag_1.id, sample_to_tag_1.tag_id, tag_1.name, tag)
            query = query.join(tag_1, and_(*tag_join_condition))

    return query


def get_gene_types(info, gene_type=None, gene_dict=dict()):
    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)
    relations = build_option_args(
        selection_set, {'geneTypes': 'gene_types'})

    if gene_dict and ('gene_types' in relations or gene_type):
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

        requested = build_option_args(
            gene_type_selection_set, {'display': 'display', 'name': 'name'})

        gene_type_query = sess.query(*gene_type_core)
        gene_type_query = gene_type_query.select_from(gene_type_1)

        gene_gene_type_join_condition = build_join_condition(
            gene_to_gene_type_1.type_id, gene_type_1.id, gene_to_gene_type_1.gene_id, [*gene_dict])

        if gene_type:
            gene_gene_type_join_condition.append(
                gene_type_1.name.in_(gene_type))

        gene_type_query = gene_type_query.join(gene_to_gene_type_1, and_(
            *gene_gene_type_join_condition))

        order = []
        if 'name' in requested:
            order.append(gene_type_1.name)
        elif 'display' in requested:
            order.append(gene_type_1.display)
        else:
            order.append(gene_type_1.id)
        gene_type_query = gene_type_query.order_by(*order)

        return gene_type_query.distinct().all()

    return []


def get_publications(info, gene_types=[], gene_dict=dict(), by_tag=False):
    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, by_tag, child_node='genes')
    relations = build_option_args(
        selection_set, {'publications': 'publications'})

    if gene_dict and 'publications' in relations:
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

        requested = build_option_args(
            pub_selection_set, {'doId': 'do_id',
                                'firstAuthorLastName': 'first_author_last_name',
                                'journal': 'journal',
                                'name': 'name',
                                'pubmedId': 'pubmed_id',
                                'title': 'title',
                                'year': 'year'})

        pub_query = sess.query(
            *pub_core, pub_gene_gene_type_1.gene_id.label('gene_id'))
        pub_query = pub_query.select_from(pub_1)

        pub_gene_gene_type_join_condition = build_pub_gene_gene_type_join_condition(
            gene_dict, gene_types, pub_gene_gene_type_1, pub_1)
        print('pub_gene_gene_type_join_condition: ',
              pub_gene_gene_type_join_condition)
        pub_query = pub_query.join(pub_gene_gene_type_1, and_(
            *pub_gene_gene_type_join_condition))

        order = []
        if 'name' in requested:
            order.append(pub_1.name)
        elif 'pubmed_id' in requested:
            order.append(pub_1.pubmed_id)
        elif 'do_id' in requested:
            order.append(pub_1.do_id)
        elif 'title' in requested:
            order.append(pub_1.title)
        elif 'first_author_last_name' in requested:
            order.append(pub_1.first_author_last_name)
        elif 'year' in requested:
            order.append(pub_1.year)
        elif 'journal' in requested:
            order.append(pub_1.journal)
        pub_query = pub_query.order_by(*order)

        return pub_query.distinct().all()

    return []


def get_samples(info, sample=None, gene_dict=dict()):
    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)
    relations = build_option_args(selection_set, {'samples': 'samples'})

    if gene_dict and 'samples' in relations:
        sess = db.session
        sample_1 = orm.aliased(Sample, name='s')
        gene_to_sample_1 = orm.aliased(GeneToSample, name='gs')

        sample_selection_set = get_selection_set(
            selection_set, ('samples' in relations), child_node='samples')
        sample_core_field_mapping = {'name': sample_1.name.label('name'),
                                     'rnaSeqExpr': gene_to_sample_1.rna_seq_expr.label('rna_seq_expr')}

        sample_core = build_option_args(
            sample_selection_set, sample_core_field_mapping)
        # Always select the sample id and the gene id.
        sample_core = sample_core + \
            [sample_1.id.label(
                'id'), gene_to_sample_1.gene_id.label('gene_id')]

        requested = build_option_args(
            sample_selection_set, {'name': 'name', 'rnaSeqExpr': 'rna_seq_expr'})

        sample_query = sess.query(*sample_core)
        sample_query = sample_query.select_from(sample_1)

        if sample:
            sample_query = sample_query.filter(sample_1.name.in_(sample))

        gene_sample_join_condition = build_join_condition(
            gene_to_sample_1.sample_id, sample_1.id, gene_to_sample_1.gene_id, [*gene_dict])

        sample_query = sample_query.join(
            gene_to_sample_1, and_(*gene_sample_join_condition))

        order = []
        if 'name' in requested:
            order.append(sample_1.name)
        elif 'rna_seq_expr' in requested:
            order.append(gene_to_sample_1.rna_seq_expr)
        sample_query = sample_query.order_by(*order)

        return sample_query.distinct().all()

    return []


def request_gene(_obj, info, entrez=None, sample=None):
    query = build_gene_request(_obj, info, entrez=[entrez], sample=sample)
    return query.one_or_none()


def request_genes(_obj, info, data_set=None, entrez=None, feature=None, feature_class=None, gene_family=None,
                  gene_function=None, gene_type=None, immune_checkpoint=None, pathway=None, related=None,
                  sample=None, super_category=None, tag=None, therapy_type=None, by_tag=False):
    genes_query = build_gene_request(_obj, info, by_tag=by_tag, data_set=data_set, entrez=entrez, feature=feature,
                                     feature_class=feature_class, gene_family=gene_family, gene_function=gene_function,
                                     gene_type=gene_type, immune_checkpoint=immune_checkpoint, pathway=pathway,
                                     related=related, sample=sample, super_category=super_category, tag=tag,
                                     therapy_type=therapy_type)
    return genes_query.distinct().all()
