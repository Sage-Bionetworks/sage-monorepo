from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToTag, Publication, Sample, SampleToTag, Tag, TagToPublication, TagToTag, Cohort, CohortToTag, CohortToSample
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_pagination_queries


simple_tag_request_fields = {
    'characteristics',
    'color',
    'longDisplay',
    'name',
    'order'
    'shortDisplay',
    'tag',
    'type'
}

tag_request_fields = simple_tag_request_fields.union({
    'publications',
    'related',
    'sampleCount',
    'samples'
})


def has_tag_fields(item, prefix='tag_'):
    if not item:
        return False
    return(get_value(item, prefix + 'id') or get_value(item, prefix + 'name') or get_value(
        item, prefix + 'characteristics') or get_value(item, prefix + 'short_display') or get_value(item, prefix + 'long_display') or get_value(item, prefix + 'type') or get_value(item, prefix + 'order'))


def build_tag_graphql_response(requested=[], sample_requested=[], publications_requested=[], related_requested=[], cohort=None, sample=None, prefix='tag_'):
    from .publication import build_publication_graphql_response
    from .sample import build_sample_graphql_response

    def f(tag):
        if not tag:
            return None

        tag_id = get_value(tag, prefix + 'id')

        sample_dict = get_samples(
            tag_id=tag_id, requested=requested, sample_requested=sample_requested, cohort=cohort, sample=sample)

        publication_dict = get_publications(
            tag_id=tag_id, requested=requested, publications_requested=publications_requested)

        related_dict = get_related(
            tag_id=tag_id, requested=requested, related_requested=related_requested)

        result = {
            'id': tag_id,
            'name': get_value(tag, prefix + 'name') or get_value(tag, 'name'),
            'characteristics': get_value(tag, prefix + 'characteristics'),
            'color': get_value(tag, prefix + 'color'),
            'longDisplay': get_value(tag, prefix + 'long_display'),
            'shortDisplay': get_value(tag, 'tag_short_display') or get_value(tag, 'short_display'),
            'type': get_value(tag, prefix + 'type'),
            'order': get_value(tag, prefix + 'order'),
            'sampleCount': len(sample_dict) if sample_dict and 'sampleCount' in requested else None,
            'publications': map(build_publication_graphql_response, publication_dict) if publication_dict else None,
            'related': map(build_tag_graphql_response(requested=related_requested), related_dict) if related_dict else None,
            'samples': map(build_sample_graphql_response(), sample_dict) if sample_dict and 'samples' in requested else None
        }
        return(result)
    return(f)


def build_tag_request(requested, distinct=False, paging=None, cohort=None, data_set=None, related=None, sample=None, tag=None, type=None):

    sess = db.session

    tag_1 = aliased(Tag, name='t')
    sample_1 = aliased(Sample, name='s')
    sample_to_tag_1 = aliased(SampleToTag, name='stt')
    dataset_to_tag_1 = aliased(DatasetToTag, name='dtt')
    dataset_1 = aliased(Dataset, name='d')
    cohort_1 = aliased(Cohort, name='c')
    cohort_to_tag_1 = aliased(CohortToTag, name='ctt')
    tag_to_tag_1 = aliased(TagToTag, name='ttt')

    core_field_mapping = {
        'characteristics': tag_1.characteristics.label('tag_characteristics'),
        'color': tag_1.color.label('tag_color'),
        'longDisplay': tag_1.long_display.label('tag_long_display'),
        'name': tag_1.name.label('tag_name'),
        'order': tag_1.order.label('tag_order'),
        'shortDisplay': tag_1.short_display.label('tag_short_display'),
        'type': tag_1.type.label('tag_type'),
    }

    core = get_selected(requested, core_field_mapping)
    core.add(tag_1.id.label('tag_id'))

    query = sess.query(*core)
    query = query.select_from(tag_1)

    if tag:
        query = query.filter(tag_1.name.in_(tag))

    if type:
        query = query.filter(tag_1.type.in_(type))

    if data_set:
        dataset_subquery = sess.query(dataset_to_tag_1.tag_id)

        dataset_join_condition = build_join_condition(
            dataset_to_tag_1.dataset_id, dataset_1.id, filter_column=dataset_1.name, filter_list=data_set)
        dataset_subquery = dataset_subquery.join(dataset_1, and_(
            *dataset_join_condition), isouter=False)

        query = query.filter(tag_1.id.in_(dataset_subquery))

    if cohort:
        cohort_subquery = sess.query(cohort_to_tag_1.tag_id)

        cohort_join_condition = build_join_condition(
            cohort_to_tag_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)
        cohort_subquery = cohort_subquery.join(cohort_1, and_(
            *cohort_join_condition), isouter=False)

        query = query.filter(tag_1.id.in_(cohort_subquery))

    if related:
        related_subquery = sess.query(tag_to_tag_1.tag_id)

        related_join_condition = build_join_condition(
            tag_to_tag_1.related_tag_id, tag_1.id, filter_column=tag_1.name, filter_list=related)
        related_subquery = related_subquery.join(tag_1, and_(
            *related_join_condition), isouter=False)

        query = query.filter(tag_1.id.in_(related_subquery))

    if sample:
        sample_subquery = sess.query(sample_to_tag_1.tag_id)

        sample_join_condition = build_join_condition(
            sample_to_tag_1.sample_id, sample_1.id, filter_column=sample_1.name, filter_list=sample)
        sample_subquery = sample_subquery.join(sample_1, and_(
            *sample_join_condition), isouter=False)

        query = query.filter(tag_1.id.in_(sample_subquery))

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

    return get_pagination_queries(query, paging, distinct, cursor_field=tag_1.id)


def build_simple_tag_request(query, requested, distinct=False, paging=None, cohort=None, data_set=None, related=None, sample=None, tag=None):
    '''
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `related` - a list of strings, tag names related to data sets
        `sample` - a list of strings, sample names
        `tag` - a list of strings, tag names related to samples
    '''
    sess = db.session

    tag_1 = aliased(Tag, name='t')
    sample_1 = aliased(Sample, name='s')
    sample_to_tag_1 = aliased(SampleToTag, name='stt')
    dataset_to_tag_1 = aliased(DatasetToTag, name='dtt')
    dataset_1 = aliased(Dataset, name='d')
    cohort_1 = aliased(Cohort, name='c')
    cohort_to_tag_1 = aliased(CohortToTag, name='ctt')
    tag_to_tag_1 = aliased(TagToTag, name='ttt')

    core_field_mapping = {
        'characteristics': tag_1.characteristics.label('tag_characteristics'),
        'color': tag_1.color.label('tag_color'),
        'longDisplay': tag_1.long_display.label('tag_long_display'),
        'name': tag_1.name.label('tag_name'),
        'shortDisplay': tag_1.short_display.label('tag_short_display')
    }

    core = get_selected(requested, core_field_mapping)
    core.add(tag_1.id.label('tag_id'))

    query = sess.query(*core)
    query = query.select_from(tag_1)

    if tag:
        query = query.filter(tag_1.name.in_(tag))

    if data_set:
        dataset_subquery = sess.query(dataset_to_tag_1.tag_id)

        dataset_join_condition = build_join_condition(
            dataset_to_tag_1.dataset_id, dataset_1.id, filter_column=dataset_1.name, filter_list=data_set)
        dataset_subquery = dataset_subquery.join(dataset_1, and_(
            *dataset_join_condition), isouter=False)

        query = query.filter(tag_1.id.in_(dataset_subquery))

    if cohort:
        cohort_subquery = sess.query(cohort_to_tag_1.tag_id)

        cohort_join_condition = build_join_condition(
            cohort_to_tag_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)
        cohort_subquery = cohort_subquery.join(cohort_1, and_(
            *cohort_join_condition), isouter=False)

        query = query.filter(tag_1.id.in_(cohort_subquery))

    if related:
        related_subquery = sess.query(tag_to_tag_1.tag_id)

        related_join_condition = build_join_condition(
            tag_to_tag_1.related_tag_id, tag_1.id, filter_column=tag_1.name, filter_list=related)
        related_subquery = related_subquery.join(tag_1, and_(
            *related_join_condition), isouter=False)

        query = query.filter(tag_1.id.in_(related_subquery))

    if sample:
        sample_subquery = sess.query(sample_to_tag_1.tag_id)

        sample_join_condition = build_join_condition(
            sample_to_tag_1.sample_id, sample_1.id, filter_column=sample_1.name, filter_list=sample)
        sample_subquery = sample_subquery.join(sample_1, and_(
            *sample_join_condition), isouter=False)

        query = query.filter(tag_1.id.in_(sample_subquery))

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

    import logging
    logger = logging.getLogger("tag request")
    logger.info(query)

    return get_pagination_queries(query, paging, distinct, cursor_field=tag_1.id)


def get_publications(tag_id, requested, publications_requested):
    if 'publications' in requested:
        sess = db.session

        pub_1 = aliased(Publication, name='p')
        tag_1 = aliased(Tag, name='t')
        tag_to_pub_1 = aliased(TagToPublication, name='tp')

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
        # Always select the publication id and the tag id.
        core |= {
            pub_1.id.label('id'),
            tag_to_pub_1.tag_id.label('tag_id')
        }

        pub_query = sess.query(*core)
        pub_query = pub_query.select_from(pub_1)

        tag_sub_query = sess.query(tag_1.id).filter(tag_1.id.in_([tag_id]))

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


def get_related(tag_id, requested, related_requested):
    if 'related' in requested:
        sess = db.session

        related_tag_1 = aliased(Tag, name='rt')
        tag_1 = aliased(Tag, name='t')
        tag_to_tag_1 = aliased(TagToTag, name='tt')

        related_core_field_mapping = {
            'characteristics': related_tag_1.characteristics.label('tag_characteristics'),
            'color': related_tag_1.color.label('tag_color'),
            'longDisplay': related_tag_1.long_display.label('tag_long_display'),
            'name': related_tag_1.name.label('tag_name'),
            'shortDisplay': related_tag_1.short_display.label('tag_short_display')
        }

        related_core = get_selected(
            related_requested, related_core_field_mapping)

        related_core |= {
            related_tag_1.id.label('id'),
            tag_to_tag_1.tag_id.label('tag_id')
        }

        related_query = sess.query(*related_core)
        related_query = related_query.select_from(related_tag_1)

        tag_sub_query = sess.query(tag_1.id).filter(tag_1.id.in_([tag_id]))

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


def get_samples(tag_id, requested, sample_requested, cohort=None, sample=None):
    if 'samples' in requested or 'sampleCount' in requested:
        sess = db.session

        sample_1 = aliased(Sample, name='s')
        sample_to_tag_1 = aliased(SampleToTag, name='stt')
        cohort_1 = aliased(Cohort, name='c')
        cohort_to_sample_1 = aliased(CohortToSample, name='cts')

        sample_core_field_mapping = {
            'name': sample_1.name.label('sample_name')}

        sample_core = get_selected(sample_requested, sample_core_field_mapping)
        sample_core |= {sample_1.id.label('sample_id')}

        sample_query = sess.query(*sample_core)
        sample_query = sample_query.select_from(sample_1)

        tag_subquery = sess.query(sample_to_tag_1.sample_id)
        tag_join_condition = build_join_condition(
            sample_to_tag_1.sample_id, sample_1.id, filter_column=sample_to_tag_1.tag_id, filter_list=[tag_id])
        tag_subquery = tag_subquery.join(cohort_1, and_(
            *tag_join_condition), isouter=False)
        sample_query = sample_query.filter(
            sample_1.id.in_(tag_subquery))

        if sample:
            sample_query = sample_query.filter(sample_1.name.in_(sample))

        if cohort:
            cohort_subquery = sess.query(cohort_to_sample_1.sample_id)

            cohort_join_condition = build_join_condition(
                cohort_to_sample_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)
            cohort_subquery = cohort_subquery.join(cohort_1, and_(
                *cohort_join_condition), isouter=False)

            sample_query = sample_query.filter(
                sample_1.id.in_(cohort_subquery))

        return sample_query.distinct().all()

    return []
