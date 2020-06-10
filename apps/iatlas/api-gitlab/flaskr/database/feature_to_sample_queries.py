from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import FeatureToSample


def return_feature_to_sample_query():
    return db.session.query(FeatureToSample)
