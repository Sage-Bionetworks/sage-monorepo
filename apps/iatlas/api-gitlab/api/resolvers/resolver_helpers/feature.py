from sqlalchemy import and_, orm
from api import db
from api.db_models import (
    Dataset, DatasetToSample, Feature, FeatureClass, FeatureToSample,
    MethodTag, Sample, SampleToTag, Tag, TagToTag)
from api.database import return_feature_query
from .general_resolvers import build_option_args, get_selection_set, get_value


def build_classes_join_condition(features_model, classes_model, feature_classes=None):
    classes_join_conditions = [features_model.class_id == classes_model.id]
    if feature_classes:
        classes_join_conditions.append(classes_model.name.in_(feature_classes))
    return classes_join_conditions


def build_feature_to_sample_join_condition(features_to_samples_model,
                                           samples_to_tags_model,
                                           feature=None):
    feature_to_sample_join_condition = [
        features_to_samples_model.sample_id == samples_to_tags_model.sample_id]
    if feature:
        chosen_feature = orm.aliased(Feature, name='cf')
        feature_to_sample_join_condition.append(features_to_samples_model.feature_id.in_(
            db.session.query(chosen_feature.id).filter(
                chosen_feature.name.in_(feature))
        ))
    return feature_to_sample_join_condition


def request_features(_obj, info, dataSet=None, related=None, feature=None, featureClass=None, byClass=False, byTag=False):
    """
    Builds a SQL request and returns values from the DB.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, byClass or byTag)

    class_1 = orm.aliased(FeatureClass, name='fc')
    feature_1 = orm.aliased(Feature, name='f')
    feature_to_sample_1 = orm.aliased(FeatureToSample, name='fs1')
    method_tag_1 = orm.aliased(MethodTag, name='mt')
    sample_1 = orm.aliased(Sample, name='s')
    tag_1 = orm.aliased(Tag, name='t')

    related_field_node_mapping = {'class': 'class',
                                  'methodTag': 'method_tag',
                                  'sample': 'sample',
                                  'value': 'value'}

    select_field_node_mapping = {'display': feature_1.display.label('display'),
                                 'name': feature_1.name.label('name'),
                                 'order': feature_1.order.label('order'),
                                 'unit': feature_1.unit.label('unit')}

    # Only select fields that were requested.
    select_fields = build_option_args(selection_set, select_field_node_mapping)
    option_args = build_option_args(selection_set, related_field_node_mapping)
    if option_args or byClass or byTag:
        join_class = 'class'
        join_method_tag = 'method_tag'
        join_sample = 'sample'
        join_value = 'value'
        if join_class in option_args or byClass:
            select_fields.append(class_1.name.label('class'))
            if join_class not in option_args:
                option_args.append(join_class)
        if byTag:
            select_fields.append(tag_1.name.label('tag'))
            select_fields.append(tag_1.display.label('tag_display'))
            select_fields.append(
                tag_1.characteristics.label('tag_characteristics'))
        if join_method_tag in option_args:
            select_fields.append(method_tag_1.name.label('method_tag'))
        if join_sample in option_args:
            select_fields.append(sample_1.name.label('sample'))
        if join_value in option_args:
            select_fields.append(feature_to_sample_1.value.label('value'))
            select_fields.append(feature_to_sample_1.inf_value.label('inf'))

    query = sess.query(*select_fields)

    if not dataSet and not related:
        query = query.select_from(feature_1)

        if feature:
            query = query.filter(feature_1.name.in_(feature))

        if 'sample' in option_args or 'value' in option_args:
            query = query.join(feature_to_sample_1,
                               feature_to_sample_1.feature_id == feature_1.id, isouter=True)
            if 'sample' in option_args:
                query = query.join(sample_1, sample_1.id ==
                                   feature_to_sample_1.sample_id, isouter=True)
    else:
        dataset_1 = orm.aliased(Dataset, name='d')
        dataset_to_sample_1 = orm.aliased(DatasetToSample, name='ds')
        related_tag = orm.aliased(Tag, name='rt')
        sample_to_tag_1 = orm.aliased(SampleToTag, name='st1')
        sample_to_tag_2 = orm.aliased(SampleToTag, name='st2')
        tag_to_tag_1 = orm.aliased(TagToTag, name='tt')

        query = query.select_from(sample_to_tag_1)

        feature_to_sample_join_condition = build_feature_to_sample_join_condition(
            feature_to_sample_1, sample_to_tag_1, feature)

        query = query.join(feature_to_sample_1, and_(
            *feature_to_sample_join_condition))

        if dataSet:
            query = query.join(dataset_to_sample_1,
                               and_(dataset_to_sample_1.sample_id == feature_to_sample_1.sample_id,
                                    dataset_to_sample_1.dataset_id.in_(
                                        sess.query(dataset_1.id).filter(
                                            dataset_1.name.in_(dataSet))
                                    )))

        if related:
            query = query.join(tag_to_tag_1,
                               and_(sample_to_tag_1.tag_id == tag_to_tag_1.related_tag_id,
                                    tag_to_tag_1.related_tag_id.in_(
                                        sess.query(related_tag.id).filter(
                                            related_tag.name.in_(related)))))

            if byTag:
                query = query.join(
                    tag_1, tag_to_tag_1.tag_id == tag_1.id, isouter=True)

            query = query.join(
                sample_to_tag_2, and_(
                    feature_to_sample_1.sample_id == sample_to_tag_2.sample_id,
                    sample_to_tag_2.tag_id == tag_to_tag_1.tag_id))

        query = query.join(feature_1, feature_1.id ==
                           feature_to_sample_1.feature_id)

        if 'sample' in option_args:
            query = query.join(
                sample_1, feature_to_sample_1.sample_id == sample_1.id, isouter=True)

    if 'class' in option_args or featureClass:
        classes_join_condition = build_classes_join_condition(
            feature_1, class_1, featureClass)
        query = query.join(class_1, and_(
            *classes_join_condition), isouter=True)

    if 'method_tag' in option_args:
        query = query.join(
            method_tag_1, feature_1.method_tag_id == method_tag_1.id, isouter=True)

    query = query.distinct()
    query = query.order_by(feature_1.order)

    return query.all()


def return_feature_value(row):
    infinity = get_value(row, 'inf')
    if infinity:
        infinity = str(infinity)
    return infinity or get_value(row, 'value')
