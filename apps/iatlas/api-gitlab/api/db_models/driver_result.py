from sqlalchemy import orm
from api import db
from . import Base
from api.enums import direction_enum


class DriverResult(Base):
    __tablename__ = 'driver_results'
    id = db.Column(db.Integer, primary_key=True)
    p_value = db.Column(db.Numeric, nullable=True)
    fold_change = db.Column(db.Numeric, nullable=True)
    log10_p_value = db.Column(db.Numeric, nullable=True)
    log10_fold_change = db.Column(db.Numeric, nullable=True)
    n_wt = db.Column(db.Integer, nullable=True)
    n_mut = db.Column(db.Integer, nullable=True)

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'features.id'), nullable=False)

    gene_id = db.Column(db.Integer, db.ForeignKey('genes.id'), nullable=False)

    mutation_code_id = db.Column(db.Integer, db.ForeignKey(
        'mutation_codes.id'), nullable=False)

    tag_id = db.Column(db.Integer, db.ForeignKey('tags.id'), nullable=False)

    dataset_id = db.Column(db.Integer, db.ForeignKey(
        'datasets.id'), nullable=False)

    feature = db.relationship(
        'Feature', backref=orm.backref('driver_results', uselist=True, lazy='noload'),
        uselist=False, primaryjoin="Feature.id==DriverResult.feature_id", lazy='noload')

    gene = db.relationship(
        'Gene', backref=orm.backref('driver_results', uselist=True, lazy='noload'),
        uselist=False, primaryjoin="Gene.id==DriverResult.gene_id", lazy='noload')

    mutation_code = db.relationship(
        'MutationCode', backref=orm.backref('driver_results', uselist=True, lazy='noload'),
        uselist=False, primaryjoin="MutationCode.id==DriverResult.mutation_code_id", lazy='noload')

    tag = db.relationship(
        'Tag', backref=orm.backref('driver_results', uselist=True, lazy='noload'),
        uselist=False, primaryjoin="Tag.id==DriverResult.tag_id", lazy='noload')

    dataSet = db.relationship(
        'Dataset', backref=orm.backref('driver_results', uselist=True, lazy='noload'),
        uselist=False, primaryjoin="Dataset.id==DriverResult.dataset_id", lazy='noload')

    def __repr__(self):
        return '<DriverResult %r>' % self.id
