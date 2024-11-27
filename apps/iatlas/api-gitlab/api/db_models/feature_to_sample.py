from sqlalchemy import orm
from api import db
from . import Base


class FeatureToSample(Base):
    __tablename__ = 'features_to_samples'

    id = db.Column(db.String, primary_key=True)
    feature_to_sample_value = db.Column(db.Numeric, nullable=True)

    feature_id = db.Column(db.String, db.ForeignKey(
        'features.id'), primary_key=True)

    sample_id = db.Column(db.String, db.ForeignKey(
        'samples.id'), primary_key=True)

    features = db.relationship('Feature', backref=orm.backref(
        'feature_sample_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    samples = db.relationship('Sample', backref=orm.backref(
        'feature_sample_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<FeatureToSample %r>' % self.feature_id
