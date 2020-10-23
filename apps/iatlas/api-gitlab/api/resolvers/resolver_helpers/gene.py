from sqlalchemy import and_
from sqlalchemy.orm import aliased
from itertools import groupby
from api import db
from api.db_models import (
    Dataset, DatasetToTag, DatasetToSample, Feature, FeatureClass, FeatureToSample, Gene, GeneFamily,
    GeneFunction, GeneToSample, GeneToType, GeneType, ImmuneCheckpoint, Pathway, Publication,
    PublicationToGeneToGeneType, SuperCategory, Sample, SampleToTag, Tag, TagToTag, TherapyType)
from .general_resolvers import build_join_condition, get_selected, get_value


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
                                                        'samples',
                                                        'superCategory',
                                                        'therapyType'})


def build_gene_graphql_response(pub_dict=dict(), sample_dict=dict(), gene_type_dict=dict()):
    def f(gene):
        if not gene:
            return None
        gene_id = get_value(gene, 'id')
        gene_types = gene_type_dict.get(gene_id, []) if gene_type_dict else []
        publications = pub_dict.get(gene_id, []) if pub_dict else []
        samples = sample_dict.get(gene_id, []) if sample_dict else []
        return {
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
            'publications': [{
                'doId': get_value(pub, 'do_id'),
                'firstAuthorLastName': get_value(pub, 'first_author_last_name'),
                'journal': get_value(pub, 'journal'),
                'name': get_value(pub),
                'pubmedId': get_value(pub, 'pubmed_id'),
                'title': get_value(pub, 'title'),
                'year': get_value(pub, 'year')
            } for pub in publications],
            'samples': [{
                'name': get_value(sample),
                'rnaSeqExpr': get_value(sample, 'rna_seq_expr')
            } for sample in samples],
            'superCategory': get_value(gene, 'super_category'),
            'therapyType': get_value(gene, 'therapy_type')
        }
    return f


def build_pub_gene_gene_type_join_condition(gene_ids, gene_types, pub_gene_gene_type_model, pub_model):
    join_condition = [
        pub_gene_gene_type_model.publication_id == pub_model.id, pub_gene_gene_type_model.gene_id.in_(gene_ids)]

    map_of_ids = list(map(lambda gt: gt.id, gene_types))
    gene_type_ids = list(dict.fromkeys(map_of_ids)) if map_of_ids else None

    if gene_type_ids:
        join_condition.append(
            pub_gene_gene_type_model.gene_type_id.in_(gene_type_ids))

    return join_condition


def build_gene_request(
        requested, tag_requested, data_set=None, entrez=None, feature=None, feature_class=None, gene_family=None, gene_function=None, gene_type=None, immune_checkpoint=None, max_rna_seq_expr=None, min_rna_seq_expr=None, pathway=None, related=None, sample=None, super_category=None, tag=None, therapy_type=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    gene_1 = aliased(Gene, name='g')
    gene_family_1 = aliased(GeneFamily, name='gf')
    gene_function_1 = aliased(GeneFunction, name='gfn')
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

    if tag_requested or tag or sample or data_set or related or feature or feature_class or max_rna_seq_expr != None or min_rna_seq_expr != None:
        gene_to_sample_1 = aliased(GeneToSample, name='gs')

        gene_to_sample_sub_query = sess.query(gene_to_sample_1.sample_id).filter(
            gene_to_sample_1.gene_id == gene_1.id)
        if max_rna_seq_expr != None:
            gene_to_sample_sub_query = gene_to_sample_sub_query.filter(
                gene_to_sample_1.rna_seq_expr <= max_rna_seq_expr)
        if min_rna_seq_expr != None:
            gene_to_sample_sub_query = gene_to_sample_sub_query.filter(
                gene_to_sample_1.rna_seq_expr >= min_rna_seq_expr)
        sample_join_condition = [sample_1.id.in_(gene_to_sample_sub_query)]

        if sample:
            sample_join_condition.extend([sample_1.name.in_(sample)])

        query = query.join(sample_1, and_(*sample_join_condition))

        if data_set or related:
            data_set_1 = aliased(Dataset, name='d')
            data_set_to_sample_1 = aliased(DatasetToSample, name='dts')

            data_set_sub_query = sess.query(data_set_1.id).filter(
                data_set_1.name.in_(data_set)) if data_set else data_set

            data_set_to_sample_join_condition = build_join_condition(
                data_set_to_sample_1.sample_id, sample_1.id, data_set_to_sample_1.dataset_id, data_set_sub_query)
            query = query.join(
                data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

        if feature or feature_class:
            feature_1 = aliased(Feature, name='f')
            feature_class_1 = aliased(FeatureClass, name='fc')
            feature_to_sample_1 = aliased(FeatureToSample, name='fs')

            query = query.join(feature_to_sample_1,
                               feature_to_sample_1.sample_id == sample_1.id)

            feature_join_condition = build_join_condition(
                feature_1.id, feature_to_sample_1.feature_id, feature_1.name, feature)
            query = query.join(feature_1, and_(*feature_join_condition))

            if feature_class:
                feature_class_join_condition = build_join_condition(
                    feature_class_1.id, feature_1.class_id, feature_class_1.name, feature_class)
                query = query.join(
                    feature_class_1, and_(*feature_class_join_condition))

        if tag_requested or tag:
            sample_to_tag_1 = aliased(SampleToTag, name='stt')
            sample_to_tag_join_condition = [
                sample_to_tag_1.sample_id == sample_1.id]

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

    order = []
    append_to_order = order.append
    if tag_requested:
        append_to_order(tag_1.name)
    if 'display' in requested:
        append_to_order(tag_1.display)
    if 'color' in requested:
        append_to_order(tag_1.color)
    if 'characteristics' in requested:
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

    return query.order_by(*order)


def get_gene_types(
        requested, gene_types_requested, data_set=None, entrez=None, feature=None, feature_class=None, gene_family=None, gene_function=None, gene_type=None, immune_checkpoint=None, max_rna_seq_expr=None, min_rna_seq_expr=None, pathway=None, related=None, sample=None, super_category=None, tag=None, therapy_type=None, gene_ids=[]):
    if 'geneTypes' in requested:
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

        genes = build_gene_request(
            set(), set(), data_set=data_set, entrez=entrez, feature=feature, feature_class=feature_class, gene_family=gene_family, gene_function=gene_function, gene_type=gene_type, immune_checkpoint=immune_checkpoint, max_rna_seq_expr=max_rna_seq_expr, min_rna_seq_expr=min_rna_seq_expr, pathway=pathway, related=related, sample=sample, super_category=super_category, tag=tag, therapy_type=therapy_type) if not gene_ids else gene_ids

        gene_gene_type_join_condition = build_join_condition(
            gene_to_gene_type_1.type_id, gene_type_1.id, gene_to_gene_type_1.gene_id, genes)

        if gene_type:
            gene_gene_type_join_condition.append(
                gene_type_1.name.in_(gene_type))

        gene_type_query = gene_type_query.join(gene_to_gene_type_1, and_(
            *gene_gene_type_join_condition))

        order = []
        append_to_order = order.append
        if 'name' in requested:
            append_to_order(gene_type_1.name)
        if 'display' in requested:
            append_to_order(gene_type_1.display)
        if not order:
            append_to_order(gene_type_1.id)
        gene_type_query = gene_type_query.order_by(*order)

        return gene_type_query.distinct().all()

    return []


def get_publications(
        requested, publications_requested, data_set=None, entrez=None, feature=None, feature_class=None, gene_family=None, gene_function=None, gene_type=[], immune_checkpoint=None, max_rna_seq_expr=None, min_rna_seq_expr=None, pathway=None, related=None, sample=None, super_category=None, tag=None, therapy_type=None, gene_ids=[]):
    if 'publications' in requested:
        sess = db.session

        gene_1 = aliased(Gene, name='g')
        gene_type_1 = aliased(GeneType, name='gt')
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

        gene_subquery = build_gene_request(
            set(), set(), data_set=data_set, entrez=entrez, feature=feature, feature_class=feature_class, gene_family=gene_family, gene_function=gene_function, gene_type=gene_type, immune_checkpoint=immune_checkpoint, max_rna_seq_expr=max_rna_seq_expr, min_rna_seq_expr=min_rna_seq_expr, pathway=pathway, related=related, sample=sample, super_category=super_category, tag=tag, therapy_type=therapy_type) if not gene_ids else gene_ids

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

    return []


def get_samples(
        requested, samples_requested, data_set=None, entrez=None, feature=None, feature_class=None, gene_family=None, gene_function=None, gene_type=None, immune_checkpoint=None, max_rna_seq_expr=None, min_rna_seq_expr=None, pathway=None, related=None, sample=None, super_category=None, tag=None, therapy_type=None, gene_ids=[]):

    if 'samples' in requested:
        sess = db.session

        data_set_to_sample_1 = aliased(DatasetToSample, name='ds')
        gene_1 = aliased(Gene, name='g')
        gene_to_sample_1 = aliased(GeneToSample, name='gs')
        sample_1 = aliased(Sample, name='s')
        sample_to_tag_1 = aliased(SampleToTag, name='st')

        core_field_mapping = {'name': sample_1.name.label('name'),
                              'rnaSeqExpr': gene_to_sample_1.rna_seq_expr.label('rna_seq_expr')}

        core = get_selected(samples_requested, core_field_mapping)
        # Always select the sample id and the gene id.
        core |= {sample_1.id.label('id'),
                 gene_to_sample_1.gene_id.label('gene_id')}

        sample_query = sess.query(*core)
        sample_query = sample_query.select_from(sample_1)

        if sample:
            sample_query = sample_query.filter(sample_1.name.in_(sample))

        gene_subquery = build_gene_request(
            set(), set(), data_set=data_set, entrez=entrez, feature=feature, feature_class=feature_class, gene_family=gene_family, gene_function=gene_function, gene_type=gene_type, immune_checkpoint=immune_checkpoint, max_rna_seq_expr=max_rna_seq_expr, min_rna_seq_expr=min_rna_seq_expr, pathway=pathway, related=related, sample=sample, super_category=super_category, tag=tag, therapy_type=therapy_type) if not gene_ids else gene_ids

        gene_sample_join_condition = build_join_condition(
            gene_to_sample_1.sample_id, sample_1.id, gene_to_sample_1.gene_id, gene_subquery)
        gene_sample_join_condition.extend(
            [gene_to_sample_1.rna_seq_expr <= max_rna_seq_expr] if max_rna_seq_expr != None else [])
        gene_sample_join_condition.extend(
            [gene_to_sample_1.rna_seq_expr >= min_rna_seq_expr] if min_rna_seq_expr != None else [])

        sample_query = sample_query.join(
            gene_to_sample_1, and_(*gene_sample_join_condition))

        if data_set or related:
            data_set_1 = aliased(Dataset, name='d')

            data_set_sub_query = sess.query(data_set_1.id).filter(
                data_set_1.name.in_(data_set)) if data_set else data_set

            data_set_to_sample_join_condition = build_join_condition(
                data_set_to_sample_1.sample_id, sample_1.id, data_set_to_sample_1.dataset_id, data_set_sub_query)
            sample_query = sample_query.join(
                data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

        if feature or feature_class:
            feature_1 = aliased(Feature, name='f')
            feature_class_1 = aliased(FeatureClass, name='fc')
            feature_to_sample_1 = aliased(FeatureToSample, name='fs')

            sample_query = sample_query.join(feature_to_sample_1,
                                             feature_to_sample_1.sample_id == sample_1.id)

            feature_join_condition = build_join_condition(
                feature_1.id, feature_to_sample_1.feature_id, feature_1.name, feature)
            sample_query = sample_query.join(
                feature_1, and_(*feature_join_condition))

            if feature_class:
                feature_class_join_condition = build_join_condition(
                    feature_class_1.id, feature_1.class_id, feature_class_1.name, feature_class)
                sample_query = sample_query.join(
                    feature_class_1, and_(*feature_class_join_condition))

        if tag or related:
            tag_1 = aliased(Tag, name='t')

            tag_sub_query = sess.query(tag_1.id).filter(
                tag_1.name.in_(tag)) if tag else tag
            sample_to_tag_join_condition = build_join_condition(
                sample_to_tag_1.sample_id, sample_1.id, sample_to_tag_1.tag_id, tag_sub_query)

        if related:
            data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
            related_tag_1 = aliased(Tag, name='rt')
            tag_to_tag_1 = aliased(TagToTag, name='tt')

            related_tag_sub_query = sess.query(related_tag_1.id).filter(
                related_tag_1.name.in_(related)) if related else related

            data_set_tag_join_condition = build_join_condition(
                data_set_to_tag_1.dataset_id, data_set_to_sample_1.dataset_id, data_set_to_tag_1.tag_id, related_tag_sub_query)
            sample_query = sample_query.join(
                data_set_to_tag_1, and_(*data_set_tag_join_condition))

            tag_to_tag_subquery = sess.query(tag_to_tag_1.tag_id).filter(
                tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id)

            sample_to_tag_join_condition.append(
                sample_to_tag_1.tag_id.in_(tag_to_tag_subquery))

        if tag or related:
            sample_query = sample_query.join(sample_to_tag_1, and_(
                *sample_to_tag_join_condition))

        order = []
        append_to_order = order.append
        if 'name' in requested:
            append_to_order(sample_1.name)
        if 'rnaSeqExpr' in requested:
            append_to_order(gene_to_sample_1.rna_seq_expr)
        sample_query = sample_query.order_by(*order) if order else sample_query

        return sample_query.distinct().all()

    return []


def request_gene(requested, **kwargs):
    '''
    Keyword arguments are:
        `entrez` - a list of integers
        `sample` - a list of strings
    '''
    query = build_gene_request(requested, set(), **kwargs)
    return query.one_or_none()


def request_genes(
        requested, tag_requested, data_set=None, entrez=None, feature=None, feature_class=None, gene_family=None, gene_function=None, gene_type=None, immune_checkpoint=None, max_rna_seq_expr=None, min_rna_seq_expr=None, pathway=None, related=None, sample=None, super_category=None, tag=None, therapy_type=None, distinct=False):
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
        `distinct` - a boolean, true if the results should have DISTINCT applied
    '''
    genes_query = build_gene_request(
        requested, tag_requested, data_set=data_set, entrez=entrez, feature=feature, feature_class=feature_class, gene_family=gene_family, gene_function=gene_function, gene_type=gene_type, immune_checkpoint=immune_checkpoint, max_rna_seq_expr=max_rna_seq_expr, min_rna_seq_expr=min_rna_seq_expr, pathway=pathway, related=related, sample=sample, super_category=super_category, tag=tag, therapy_type=therapy_type)
    if distinct:
        genes_query = genes_query.distinct()
    return genes_query.all()


def return_gene_derived_fields(requested, gene_types_requested, publications_requested, samples_requested, **kwargs):
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
    samples = get_samples(requested, samples_requested, **kwargs)
    gene_types = get_gene_types(requested, gene_types_requested, **kwargs)
    pubs = get_publications(
        requested, publications_requested, **dict(kwargs, gene_type=gene_types))

    types_dict = dict()
    for key, collection in groupby(gene_types, key=lambda gt: gt.gene_id):
        types_dict[key] = types_dict.get(key, []) + list(collection)

    samples_dict = dict()
    for key, collection in groupby(samples, key=lambda s: s.gene_id):
        samples_dict[key] = samples_dict.get(key, []) + list(collection)

    pubs_dict = dict()
    for key, collection in groupby(pubs, key=lambda pub: pub.gene_id):
        pubs_dict[key] = pubs_dict.get(key, []) + list(collection)

    return (pubs_dict, samples_dict, types_dict)
