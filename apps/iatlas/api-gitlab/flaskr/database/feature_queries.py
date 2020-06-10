from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import FeatureClass, Feature, MethodTag


def return_feature_class_query():
    return db.session.query(FeatureClass)


def return_feature_query():
    return db.session.query(Feature)


def return_method_tag_query():
    return db.session.query(MethodTag)
