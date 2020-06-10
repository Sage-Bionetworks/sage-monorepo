from flaskr import db
from . import Base


class FeatureToSample(Base):
    __tablename__ = 'features_to_samples'

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'features.id'), primary_key=True)

    sample_id = db.Column(db.Integer, db.ForeignKey(
        'samples.id'), nullable=False)

    value = db.Column(db.Float, nullable=True)

    inf_value = db.Column(db.Float, nullable=True)

    def __repr__(self):
        return '<FeatureToSample %r>' % self.feature_id
