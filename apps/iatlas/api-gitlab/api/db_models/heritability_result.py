from sqlalchemy import orm
from api import db
from . import Base


class HeritabilityResult(Base):
    __tablename__ = 'heritability_results'
    id = db.Column(db.Integer, primary_key=True)
    p_value = db.Column(db.Numeric, nullable=True)
    fdr = db.Column(db.Numeric, nullable=True)
    variance = db.Column(db.Numeric, nullable=True)
    se = db.Column(db.Numeric, nullable=True)
    cluster = db.Column(db.String, nullable=False)

    dataset_id = db.Column(db.Integer, db.ForeignKey(
        'datasets.id'), nullable=False)

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'features.id'), nullable=False)

    data_set = db.relationship(
        'Dataset', backref=orm.backref('heritability_results', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    feature = db.relationship(
        'Feature', backref=orm.backref('heritability_results', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    def __repr__(self):
        return '<HeritabilityResult %r>' % self.id
