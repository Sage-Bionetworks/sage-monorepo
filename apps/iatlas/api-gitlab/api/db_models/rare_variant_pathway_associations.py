from sqlalchemy import orm
from api import db
from . import Base


class RareVariantPathwayAssociation(Base):
    __tablename__ = 'rare_variant_pathway_associations'
    id = db.Column(db.Integer, primary_key=True)
    pathway = db.Column(db.String)
    p_value = db.Column(db.Numeric, nullable=True)
    min = db.Column(db.Numeric, nullable=True)
    max = db.Column(db.Numeric, nullable=True)
    mean = db.Column(db.Numeric, nullable=True)
    q1 = db.Column(db.Numeric, nullable=True)
    q2 = db.Column(db.Numeric, nullable=True)
    q3 = db.Column(db.Numeric, nullable=True)
    n_mutants = db.Column(db.Integer, nullable=True)
    n_total = db.Column(db.Integer, nullable=True)

    dataset_id = db.Column(db.Integer, db.ForeignKey(
        'datasets.id'), nullable=False)

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'features.id'), nullable=False)

    data_set = db.relationship(
        'Dataset', backref=orm.backref('rare_variant_pathway_associations', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    feature = db.relationship(
        'Feature', backref=orm.backref('rare_variant_pathway_associations', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    def __repr__(self):
        return '<RareVariantPathwayAssociation %r>' % self.id
