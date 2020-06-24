from sqlalchemy import orm
from flaskr import db
from . import Base


class DatasetToSample(Base):
    __tablename__ = 'datasets_to_samples'

    dataset_id = db.Column(
        db.Integer, db.ForeignKey('datasets.id'), primary_key=True)

    sample_id = db.Column(
        db.Integer, db.ForeignKey('samples.id'), nullable=False)

    datasets = db.relationship('Dataset', backref=orm.backref(
        'dataset_sample_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    samples = db.relationship('Sample', backref=orm.backref(
        'dataset_sample_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<DatasetToSample %r>' % self.dataset_id
