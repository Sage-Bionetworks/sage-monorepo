from sqlalchemy import orm
from api import db
from . import Base


class FeatureToSampleJoined(Base):
    __tablename__ = 'features_to_samples_joined'

    id = db.Column(db.Integer, primary_key=True)
    value = db.Column(db.Numeric, nullable=True)
    feature_name = db.Column(db.String, nullable=False)
    feature_display = db.Column(db.String, nullable=False)
    feature_order = db.Column(db.Integer, nullable=True)
    class_name = db.Column(db.String, nullable=False)
    sample_name = db.Column(db.String, nullable=False)

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'features.id'), primary_key=True)

    sample_id = db.Column(db.Integer, db.ForeignKey(
        'samples.id'), primary_key=True)

    features = db.relationship('Feature', backref=orm.backref(
        'feature_sample_joined_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    samples = db.relationship('Sample', backref=orm.backref(
        'feature_sample_joined_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<FeatureToSampleJoined %r>' % self.id
