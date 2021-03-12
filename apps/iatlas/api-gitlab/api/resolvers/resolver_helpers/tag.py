from itertools import groupby
from sqlalchemy import and_, func
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToTag, DatasetToSample, Feature, FeatureClass, FeatureToSample, Publication, Sample, SampleToTag, Tag, TagToPublication, TagToTag
from .general_resolvers import build_join_condition, get_selected, get_value
from .publication import build_publication_graphql_response

related_request_fields = {'dataSet',
                          'display',
                          'related'}

simple_tag_request_fields = {'characteristics',
                             'color',
                             'longDisplay',
                             'name',
                             'shortDisplay',
                             'tag'}

tag_request_fields = simple_tag_request_fields.union({'publications',
                                                      'related',
                                                      'sampleCount',
                                                      'samples'})


def build_related_graphql_response(related_set=set()):
    data_set, related_tag = related_set
    return {
        'display': get_value(related_tag[0], 'data_set_display'),
        'dataSet': data_set,
        'related': list(map(build_tag_graphql_response(), related_tag))
    }


def build_related_request(requested, related_requested, data_set=None, related=None, by_data_set=True):
    '''
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request. The request is typically made for data set values with a 'related' child node.
        2nd position - a set of the requested fields in the 'related' node of the graphql request. If 'related' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `related` - a list of strings, tag names related to data sets
        `by_data_set` - a boolean, True if the returned related tags are by data set. This defaults to True.
    '''
    sess = db.session

    related_1 = aliased(Tag, name='t')
    data_set_1 = aliased(Dataset, name='d')

    core_field_mapping = {'characteristics': related_1.characteristics.label('characteristics'),
                          'color': related_1.color.label('color'),
                          'longDisplay': related_1.long_display.label('long_display'),
                          'name': related_1.name.label('name'),
                          'shortDisplay': related_1.short_display.label('short_display')}
    data_set_core_field_mapping = {
        'display': data_set_1.display.label('data_set_display')}

    core = get_selected(related_requested, core_field_mapping)
    data_set_core = get_selected(requested, data_set_core_field_mapping)

    if by_data_set or 'dataSet' in requested:
        data_set_core.add(data_set_1.name.label('data_set'))

    query = sess.query(*[*core, *data_set_core])

    if related:
        query = query.filter(related_1.name.in_(related))

    if data_set or by_data_set or 'dataSet' in requested:
        data_set_to_tag_1 = aliased(DatasetToTag, name='dt')

        query = query.join(data_set_to_tag_1,
                           data_set_to_tag_1.tag_id == related_1.id)

        data_set_join_condition = build_join_condition(
            data_set_1.id, data_set_to_tag_1.dataset_id, data_set_1.name, data_set)
        query = query.join(data_set_1, and_(*data_set_join_condition))

    order = []
    append_to_order = order.append
    if 'name' in related_requested:
        append_to_order(related_1.name)
    if 'shortDisplay' in related_requested:
        append_to_order(related_1.short_display)
    if 'longDisplay' in related_requested:
        append_to_order(related_1.long_display)
    if 'color' in related_requested:
        append_to_order(related_1.color)
    if 'characteristics' in related_requested:
        append_to_order(related_1.characteristics)

    query = query.order_by(*order) if order else query

    return query


def build_tag_graphql_response(publication_dict=dict(), related_dict=dict(), sample_dict=dict()):
    def f(tag):
        if not tag:
            return None
        tag_id = get_value(tag, 'id')
        publications = publication_dict.get(
            tag_id, []) if publication_dict else []
        related = related_dict.get(tag_id, []) if related_dict else []
        samples = sample_dict.get(tag_id, []) if sample_dict else []
        return {
            'characteristics': get_value(tag, 'characteristics'),
            'color': get_value(tag, 'color'),
            'longDisplay': get_value(tag, 'tag_long_display') or get_value(tag, 'long_display'),
            'publications': map(build_publication_graphql_response, publications),
            'name': get_value(tag, 'tag_name') or get_value(tag, 'name'),
            'related': [build_tag_graphql_response()(r) for r in related],
            'sampleCount': get_value(tag, 'sample_count'),
            'samples': [sample.name for sample in samples],
            'shortDisplay': get_value(tag, 'tag_short_display') or get_value(tag, 'short_display')
        }
    return f


def build_tag_request(
        requested, data_set=None, feature=None, feature_class=None, related=None, sample=None, tag=None):
    '''
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `feature` - a list of strings, feature names
        `feature_class` - a list of strings, feature class names
        `related` - a list of strings, tag names related to data sets
        `sample` - a list of strings, sample names
        `tag` - a list of strings, tag names related to samples
    '''
    sess = db.session

    tag_1 = aliased(Tag, name='t')
    sample_to_tag_1 = aliased(SampleToTag, name='st')

    core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                          'color': tag_1.color.label('color'),
                          'longDisplay': tag_1.long_display.label('long_display'),
                          'name': tag_1.name.label('name'),
                          'sampleCount': func.count(func.distinct(sample_to_tag_1.sample_id)).label('sample_count'),
                          'shortDisplay': tag_1.short_display.label('short_display'),
                          'tag': tag_1.name.label('tag')}

    # Only select fields that were requested.
    core = get_selected(requested, core_field_mapping)
    core.add(tag_1.id.label('id'))

    query = sess.query(*core)
    query = query.select_from(tag_1)

    if tag:
        query = query.filter(tag_1.name.in_(tag))

    if data_set or feature or feature_class or related or sample or ('sampleCount' in requested):
        sample_1 = aliased(Sample, name='s')
        data_set_to_sample_1 = aliased(DatasetToSample, name='dts')

        is_outer = not bool(sample)

        sample_sub_query = sess.query(sample_1.id).filter(
            sample_1.name.in_(sample)) if sample else None

        sample_tag_join_condition = build_join_condition(
            sample_to_tag_1.tag_id, tag_1.id, sample_to_tag_1.sample_id, sample_sub_query)
        query = query.join(
            sample_to_tag_1, and_(*sample_tag_join_condition), isouter=is_outer)

        if data_set or related:
            data_set_1 = aliased(Dataset, name='d')

            data_set_sub_query = sess.query(data_set_1.id).filter(
                data_set_1.name.in_(data_set)) if data_set else data_set

            data_set_to_sample_join_condition = build_join_condition(
                data_set_to_sample_1.sample_id, sample_to_tag_1.sample_id, data_set_to_sample_1.dataset_id, data_set_sub_query)
            query = query.join(
                data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

        if feature or feature_class:
            feature_1 = aliased(Feature, name='f')
            feature_class_1 = aliased(FeatureClass, name='fc')
            feature_to_sample_1 = aliased(FeatureToSample, name='fs')

            query = query.join(feature_to_sample_1,
                               feature_to_sample_1.sample_id == sample_to_tag_1.sample_id)

            feature_join_condition = build_join_condition(
                feature_1.id, feature_to_sample_1.feature_id, feature_1.name, feature)
            query = query.join(feature_1, and_(*feature_join_condition))

            if feature_class:
                feature_class_join_condition = build_join_condition(
                    feature_class_1.id, feature_1.class_id, feature_class_1.name, feature_class)
                query = query.join(
                    feature_class_1, and_(*feature_class_join_condition))

        if related:
            data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
            related_tag_1 = aliased(Tag, name='rt')
            tag_to_tag_1 = aliased(TagToTag, name='tt')

            related_tag_sub_query = sess.query(related_tag_1.id).filter(
                related_tag_1.name.in_(related))

            data_set_tag_join_condition = build_join_condition(
                data_set_to_tag_1.dataset_id, data_set_to_sample_1.dataset_id, data_set_to_tag_1.tag_id, related_tag_sub_query)
            query = query.join(
                data_set_to_tag_1, and_(*data_set_tag_join_condition))

            query = query.join(tag_to_tag_1, and_(
                tag_to_tag_1.tag_id == tag_1.id, tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id))

        if 'sampleCount' in requested:
            group_by = set()
            add_to_group_by = group_by.add
            if 'name' in requested:
                add_to_group_by(tag_1.name)
            if 'display' in requested:
                add_to_group_by(tag_1.display)
            if 'color' in requested:
                add_to_group_by(tag_1.color)
            if 'characteristics' in requested:
                add_to_group_by(tag_1.characteristics)
            add_to_group_by(tag_1.id)

            query = query.group_by(*group_by)

    order = []
    append_to_order = order.append
    if 'name' in requested:
        append_to_order(tag_1.name)
    if 'shortDisplay' in requested:
        append_to_order(tag_1.short_display)
    if 'longDisplay' in requested:
        append_to_order(tag_1.long_display)
    if 'color' in requested:
        append_to_order(tag_1.color)
    if 'characteristics' in requested:
        append_to_order(tag_1.characteristics)

    query = query.order_by(*order) if order else query

    return query


def get_publications(requested, publications_requested, tag_ids=set()):
    if 'publications' in requested:
        sess = db.session

        pub_1 = aliased(Publication, name='p')
        tag_1 = aliased(Tag, name='t')
        tag_to_pub_1 = aliased(TagToPublication, name='tp')

        core_field_mapping = {'doId': pub_1.do_id.label('do_id'),
                              'firstAuthorLastName': pub_1.first_author_last_name.label('first_author_last_name'),
                              'journal': pub_1.journal.label('journal'),
                              'name': pub_1.name.label('name'),
                              'pubmedId': pub_1.pubmed_id.label('pubmed_id'),
                              'title': pub_1.title.label('title'),
                              'year': pub_1.year.label('year')}

        core = get_selected(publications_requested, core_field_mapping)
        # Always select the publication id and the tag id.
        core |= {pub_1.id.label('id'),
                 tag_to_pub_1.tag_id.label('tag_id')}

        pub_query = sess.query(*core)
        pub_query = pub_query.select_from(pub_1)

        tag_sub_query = sess.query(tag_1.id).filter(tag_1.id.in_(tag_ids))

        tag_tag_join_condition = build_join_condition(
            tag_to_pub_1.publication_id, pub_1.id, tag_to_pub_1.tag_id, tag_sub_query)
        pub_query = pub_query.join(
            tag_to_pub_1, and_(*tag_tag_join_condition))

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


def get_related(requested, related_requested, tag_ids=set()):
    if 'related' in requested:
        sess = db.session

        related_tag_1 = aliased(Tag, name='rt')
        tag_1 = aliased(Tag, name='t')
        tag_to_tag_1 = aliased(TagToTag, name='tt')

        related_core_field_mapping = {
            'characteristics': related_tag_1.characteristics.label('characteristics'),
            'color': related_tag_1.color.label('color'),
            'longDisplay': related_tag_1.long_display.label('long_display'),
            'name': related_tag_1.name.label('name'),
            'shortDisplay': related_tag_1.short_display.label('short_display')}

        related_core = get_selected(
            related_requested, related_core_field_mapping)
        # Always select the related id and the tag id.
        related_core |= {related_tag_1.id.label(
            'id'), tag_to_tag_1.tag_id.label('tag_id')}

        related_query = sess.query(*related_core)
        related_query = related_query.select_from(related_tag_1)

        tag_sub_query = sess.query(tag_1.id).filter(tag_1.id.in_(tag_ids))

        tag_tag_join_condition = build_join_condition(
            tag_to_tag_1.related_tag_id, related_tag_1.id, tag_to_tag_1.tag_id, tag_sub_query)
        related_query = related_query.join(
            tag_to_tag_1, and_(*tag_tag_join_condition))

        order = []
        append_to_order = order.append
        if 'name' in related_requested:
            append_to_order(related_tag_1.name)
        if 'shortDisplay' in related_requested:
            append_to_order(related_tag_1.short_display)
        if 'longDisplay' in related_requested:
            append_to_order(related_tag_1.long_display)
        if 'color' in related_requested:
            append_to_order(related_tag_1.color)
        if 'characteristics' in related_requested:
            append_to_order(related_tag_1.characteristics)

        related_query = related_query.order_by(
            *order) if order else related_query

        return related_query.distinct().all()

    return []


def get_samples(requested, data_set=None, feature=None, feature_class=None, related=None, sample=None, tag_ids=set()):
    if tag_ids and 'samples' in requested:
        sess = db.session

        data_set_to_sample_1 = aliased(DatasetToSample, name='ds')
        sample_1 = aliased(Sample, name='s')
        sample_to_tag_1 = aliased(SampleToTag, name='st')

        # Always select the sample id and the gene id.
        sample_core = {sample_1.id.label('id'),
                       sample_1.name.label('name'),
                       sample_to_tag_1.tag_id.label('tag_id')}

        sample_query = sess.query(*sample_core)
        sample_query = sample_query.select_from(sample_1)

        if sample:
            sample_query = sample_query.filter(sample_1.name.in_(sample))

        sample_tag_join_condition = build_join_condition(
            sample_to_tag_1.sample_id, sample_1.id, sample_to_tag_1.tag_id, tag_ids)

        sample_query = sample_query.join(
            sample_to_tag_1, and_(*sample_tag_join_condition))

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

            sample_query = sample_query.join(tag_to_tag_1, and_(
                tag_to_tag_1.tag_id == sample_to_tag_1.tag_id, tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id))

        sample_query = sample_query.order_by(sample_1.name)

        return sample_query.distinct().all()

    return []


def request_related(requested, related_requested, **kwargs):
    '''
    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request. The request is typically made for data set values with a 'related' child node.
        2nd position - a set of the requested fields in the 'related' node of the graphql request. If 'related' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `related` - a list of strings, tag names related to data sets
        `by_data_set` - a boolean, True if the returned related tags are by data set.
    '''
    query = build_related_request(
        requested, related_requested, **kwargs)

    return query.distinct().all()


def request_tags(requested, **kwargs):
    '''
    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `feature` - a list of strings, feature names
        `feature_class` - a list of strings, feature class names
        `related` - a list of strings, tag names related to data sets
        `sample` - a list of strings, sample names
        `tag` - a list of strings, tag names related to samples
    '''
    query = build_tag_request(requested, **kwargs)

    return query.distinct().all()


def return_tag_derived_fields(requested, publications_requested, related_requested, data_set=None, feature=None, feature_class=None, related=None, sample=None, tag_ids=None):
    publications = get_publications(
        requested, publications_requested, tag_ids=tag_ids)

    publication_dict = dict()
    for key, collection in groupby(publications, key=lambda r: r.tag_id):
        publication_dict[key] = publication_dict.get(
            key, []) + list(collection)

    related_tags = get_related(requested, related_requested, tag_ids=tag_ids)

    related_dict = dict()
    for key, collection in groupby(related_tags, key=lambda r: r.tag_id):
        related_dict[key] = related_dict.get(key, []) + list(collection)

    samples = get_samples(
        requested, data_set=data_set, feature=feature, feature_class=feature_class, related=related, sample=sample, tag_ids=tag_ids)

    sample_dict = dict()
    for key, collection in groupby(samples, key=lambda s: s.tag_id):
        sample_dict[key] = sample_dict.get(key, []) + list(collection)

    return (publication_dict, related_dict, sample_dict)
