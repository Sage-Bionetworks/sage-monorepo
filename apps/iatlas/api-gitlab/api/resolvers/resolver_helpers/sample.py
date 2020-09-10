from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import (Dataset, DatasetToSample, DatasetToTag, Feature, FeatureClass, FeatureToSample,
                           Patient, Sample, SampleToMutation, SampleToTag, Tag, TagToTag)
from .general_resolvers import build_join_condition, build_option_args, get_selection_set, get_value


def build_sample_graphql_response(sample):
    return {
        'name': get_value(sample, 'name'),
        'patient': {
            'age_at_diagnosis': get_value(sample, 'age_at_diagnosis'),
            'barcode': get_value(sample, 'barcode'),
            'ethnicity': get_value(sample, 'ethnicity'),
            'gender': get_value(sample, 'gender'),
            'height': get_value(sample, 'height'),
            'race': get_value(sample, 'race'),
            'weight': get_value(sample, 'weight')
        }
    }


def build_sample_mutation_join_condition(sample_to_mutation_model, sample_model, mutation_status, mutation_id=None):
    join_condition = build_join_condition(sample_to_mutation_model.sample_id, sample_model.id,
                                          filter_column=sample_to_mutation_model.mutation_id, filter_list=mutation_id)
    if mutation_status:
        join_condition.append(
            sample_to_mutation_model.status == mutation_status)
    return join_condition


def build_sample_request(_obj, info, data_set=None, feature=None, feature_class=None, mutation_id=None, mutation_status=None,
                         patient=None, related=None, sample=None, tag=None, by_status=False, by_tag=False):
    """
    Builds a SQL query.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, (by_tag or by_status), child_node='samples')

    tag_or_status_selection_set = get_selection_set(
        info.field_nodes[0].selection_set, False)

    data_set_to_sample_1 = aliased(DatasetToSample, name='ds')
    patient_1 = aliased(Patient, name='p')
    sample_1 = aliased(Sample, name='s')
    sample_to_mutation_1 = aliased(SampleToMutation, name='sm')
    tag_1 = aliased(Tag, name='t')

    core_field_mapping = {'name': sample_1.name.label('name')}
    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('tag_characteristics'),
                              'color': tag_1.color.label('tag_color'),
                              'display': tag_1.display.label('tag_display')}
    core_requested_field_mapping = {'name': 'name',
                                    'patient': 'patient'}
    requested_field_mapping = {'characteristics': 'characteristics',
                               'color': 'color',
                               'display': 'display',
                               'status': 'status',
                               'tag': 'tag'}

    core_requested = build_option_args(
        selection_set, core_requested_field_mapping)
    requested = build_option_args(
        tag_or_status_selection_set, requested_field_mapping) if by_status or by_tag else []
    # Only select fields that were requested.
    core = build_option_args(selection_set, core_field_mapping)
    core.add(sample_1.id.label('id'))

    if by_status:
        core.add(sample_to_mutation_1.status.label('status'))

    if by_tag:
        core |= build_option_args(
            tag_or_status_selection_set, tag_core_field_mapping)
        core.add(tag_1.name.label('tag'))

    if 'patient' in core_requested:
        patient_selection_set = get_selection_set(
            selection_set, child_node='patient')
        patient_core_field_mapping = {'age_at_diagnosis': patient_1.age_at_diagnosis.label('age_at_diagnosis'),
                                      'barcode': patient_1.barcode.label('barcode'),
                                      'ethnicity': patient_1.ethnicity.label('ethnicity'),
                                      'gender': patient_1.gender.label('gender'),
                                      'height': patient_1.height.label('height'),
                                      'race': patient_1.race.label('race'),
                                      'weight': patient_1.weight.label('weight')}
        core |= build_option_args(
            patient_selection_set, patient_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(sample_1)

    if sample:
        query = query.filter(sample_1.name.in_(sample))

    if 'patient' in core_requested or patient:
        is_outer = not bool(patient)
        patient_join_condition = build_join_condition(
            patient_1.id, sample_1.patient_id, filter_column=patient_1.barcode, filter_list=patient)
        query = query.join(patient_1, and_(
            *patient_join_condition), isouter=is_outer)

    if by_status:
        sample_mutation_join_condition = build_sample_mutation_join_condition(
            sample_to_mutation_1, sample_1, mutation_status, mutation_id)
        query = query.join(sample_to_mutation_1, and_(
            *sample_mutation_join_condition))

    if by_tag or related or tag:
        sample_to_tag_1 = aliased(SampleToTag, name='st')

        query = query.join(
            sample_to_tag_1, sample_to_tag_1.sample_id == sample_1.id)

        is_outer = not bool(tag)
        tag_join_condition = build_join_condition(
            tag_1.id, sample_to_tag_1.tag_id, tag_1.name, tag)
        query = query.join(tag_1, and_(*tag_join_condition), isouter=is_outer)

    if data_set or related:
        data_set_1 = aliased(Dataset, name='d')

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

    order = []
    append_to_order = order.append
    if 'name' in core_requested:
        append_to_order(sample_1.name)
    if 'name' in requested:
        append_to_order(tag_1.name)
    if 'display' in requested:
        append_to_order(tag_1.display)
    if 'color' in requested:
        append_to_order(tag_1.color)
    if 'characteristics' in requested:
        append_to_order(tag_1.characteristics)
    if 'status' in requested:
        append_to_order(sample_to_mutation_1.status)

    query = query.order_by(*order) if order else query

    return query


def request_samples(_obj, info, data_set=None, feature=None, feature_class=None, mutation_id=None, mutation_status=None,
                    patient=None, related=None, sample=None, tag=None, by_status=False, by_tag=False):
    query = build_sample_request(_obj, info, data_set=data_set, feature=feature, feature_class=feature_class, mutation_id=mutation_id,
                                 mutation_status=mutation_status, patient=patient, related=related, sample=sample, tag=tag, by_status=by_status, by_tag=by_tag)
    return query.distinct().all()
