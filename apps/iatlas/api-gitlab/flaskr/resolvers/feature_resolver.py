from sqlalchemy import and_, func, or_, orm
import json
from collections import defaultdict
from flaskr import db
from flaskr.db_models import (
    Feature, FeatureClass, FeatureToSample, MethodTag, Sample, SampleToTag, Tag, TagToTag)
from flaskr.database import return_sample_to_tag_query, return_tag_query, return_tag_to_tag_query
from .resolver_helpers import build_option_args, get_child_value, get_value, NoneType


def request_features(_obj, info, dataSet=None, related=None, feature=None, byClass=False):
    sess = db.session

    # An example of the full query in SQL:
    # SELECT
    #     feature_1."name" AS "name",
    #     feature_1.display AS display,
    #     feature_1."order" AS "order",
    #     feature_1.unit AS unit,
    #     class_1.name AS class,
    #     method_tag_1.name AS method_tag
    # FROM samples_to_tags AS samples_to_tags_1
    # INNER JOIN features_to_samples AS features_to_samples_1 ON features_to_samples_1.sample_id = samples_to_tags_1.sample_id AND features_to_samples_1.feature_id
    #     IN(SELECT chosen_features.id FROM features AS chosen_features WHERE chosen_features."name" IN('Neutrophils_Aggregate2'))
    # INNER JOIN tags_to_tags AS tags_to_tags_1 ON samples_to_tags_1.tag_id = tags_to_tags_1.tag_id AND tags_to_tags_1.related_tag_id
    #     IN(SELECT dataset_tag.id FROM tags AS dataset_tag WHERE dataset_tag."name" IN('TCGA'))
    # INNER JOIN tags_to_tags AS tags_to_tags_2 ON samples_to_tags_1.tag_id = tags_to_tags_2.related_tag_id AND tags_to_tags_2.related_tag_id
    #     IN(SELECT related_tag.id FROM tags AS related_tag WHERE related_tag."name" IN('Immune_Subtype'))
    # INNER JOIN samples_to_tags AS samples_to_tags_2 ON samples_to_tags_2.sample_id = samples_to_tags_1.sample_id
    #     AND tags_to_tags_2.tag_id = samples_to_tags_2.tag_id
    # JOIN features AS feature_1 ON feature_1.id = features_to_samples_1.feature_id
    # JOIN classes AS class_1 ON class_1.id = feature_1.class_id
    # JOIN method_tags AS method_tag_1 ON method_tag_1.id = feature_1.method_tag_id

    feature_1 = orm.aliased(Feature, name='f')
    class_1 = orm.aliased(FeatureClass, name='fc')
    method_tag_1 = orm.aliased(MethodTag, name='mt')
    dataset_tag = orm.aliased(Tag, name='dt')
    related_tag = orm.aliased(Tag, name='rt')
    features_to_samples_1 = orm.aliased(FeatureToSample, name='fs1')
    samples_to_tags_1 = orm.aliased(SampleToTag, name='st1')
    samples_to_tags_2 = orm.aliased(SampleToTag, name='st2')
    tags_to_tags_2 = orm.aliased(TagToTag, name='tt2')

    related_field_node_mapping = {'class': 'class',
                                  'methodTag': 'method_tag'}

    select_field_node_mapping = {'display': feature_1.display.label('display'),
                                 'name': feature_1.name.label('name'),
                                 'order': feature_1.order.label('order'),
                                 'unit': feature_1.unit.label('unit')}

    features_to_samples_join_conditions = [
        features_to_samples_1.sample_id == samples_to_tags_1.sample_id]

    selection_set = info.field_nodes[0].selection_set

    if byClass and type(selection_set) is not NoneType:
        for selection in selection_set.selections:
            if selection.name.value == 'features':
                selection_set = selection.selection_set
                break

    # Only select fields that were requested.
    select_fields = build_option_args(selection_set, select_field_node_mapping)
    option_args = build_option_args(selection_set, related_field_node_mapping)
    if option_args or byClass:
        join_class = 'class'
        join_method_tag = 'method_tag'
        if join_class in option_args or byClass:
            select_fields.append(class_1.name.label('class'))
            option_args.append(join_class)
        if join_method_tag in option_args:
            select_fields.append(method_tag_1.name.label('method_tag'))

    query = sess.query(*select_fields)
    query = query.select_from(samples_to_tags_1)

    if type(feature) is not NoneType:
        chosen_feature = orm.aliased(Feature, name='cf')
        features_to_samples_join_conditions.append(features_to_samples_1.feature_id.in_(
            sess.query(chosen_feature.id).filter(
                chosen_feature.name.in_(feature))
        ))

    query = query.join(features_to_samples_1, and_(
        *features_to_samples_join_conditions))

    if type(dataSet) is not NoneType:
        tags_to_tags_1 = orm.aliased(TagToTag, name='tt1')
        query = query.join(tags_to_tags_1,
                           and_(samples_to_tags_1.tag_id == tags_to_tags_1.tag_id,
                                tags_to_tags_1.related_tag_id.in_(
                                    sess.query(dataset_tag.id).filter(
                                        dataset_tag.name.in_(dataSet))
                                )))

    if type(related) is not NoneType:
        query = query.join(tags_to_tags_2,
                           and_(samples_to_tags_1.tag_id == tags_to_tags_2.related_tag_id,
                                tags_to_tags_2.related_tag_id.in_(
                                    sess.query(related_tag.id).filter(
                                        related_tag.name.in_(related)))))

    if type(dataSet) is not NoneType or type(related) is not NoneType:
        if type(related) is NoneType:
            tags_to_tags_2 = tags_to_tags_1
        query = query.join(samples_to_tags_2,
                           and_(samples_to_tags_2.sample_id == samples_to_tags_1.sample_id,
                                tags_to_tags_2.tag_id == samples_to_tags_2.tag_id))

    query = query.join(feature_1, feature_1.id ==
                       features_to_samples_1.feature_id)

    if 'class' in option_args:
        query = query.join(class_1, feature_1.class_id == class_1.id)

    if 'method_tag' in option_args:
        query = query.join(
            method_tag_1, feature_1.method_tag_id == method_tag_1.id)

    query = query.distinct()
    query = query.order_by(feature_1.order)

    return query.all()


def resolve_features(_obj, info, dataSet=None, related=None, feature=None):
    results = request_features(_obj, info, dataSet, related, feature)
    return [{
        'class': get_value(row, 'class'),
        'display': get_value(row, 'display'),
        'methodTag': get_value(row, 'method_tag'),
        'name': get_value(row, 'name'),
        'order': get_value(row, 'order'),
        'unit': get_value(row, 'unit')
    } for row in results]


def resolve_features_by_class(_obj, info, dataSet=None, related=None, feature=None):
    results = request_features(
        _obj, info, dataSet, related, feature, byClass=True)

    class_map = dict()
    for row in results:
        feature_class = get_value(row, 'class')
        if not feature_class in class_map:
            class_map[feature_class] = [row]
        else:
            class_map[feature_class].append(row)

    return [{
        'class': key,
        'features': [{
            'class': get_value(row, 'class'),
            'display': get_value(row, 'display'),
            'methodTag': get_value(row, 'method_tag'),
            'name': get_value(row, 'name'),
            'order': get_value(row, 'order'),
            'unit': get_value(row, 'unit')
        } for row in value],
    } for key, value in class_map.items()]
