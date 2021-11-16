from sqlalchemy import orm
from api import db
from . import Base


class CohortToFeature(Base):
    __tablename__ = 'cohorts_to_features'

    id = db.Column(db.Integer, primary_key=True)

    cohort_id = db.Column(db.Integer, db.ForeignKey(
        'cohorts.id'), primary_key=True)

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'features.id'), primary_key=True)

    cohort = db.relationship('Cohort', backref=orm.backref(
        'cohort_feature_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    feature = db.relationship('Feature', backref=orm.backref(
        'cohort_feature_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    def __repr__(self):
        return '<CohortToFeature %r>' % self.id
