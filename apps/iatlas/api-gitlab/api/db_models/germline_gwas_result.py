from sqlalchemy import orm
from api import db
from . import Base


class GermlineGwasResult(Base):
    __tablename__ = 'germline_gwas_results'
    id = db.Column(db.Integer, primary_key=True)
    p_value = db.Column(db.Numeric, nullable=True)
    maf = db.Column(db.Numeric, nullable=True)

    dataset_id = db.Column(db.Integer, db.ForeignKey(
        'datasets.id'), nullable=False)

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'features.id'), nullable=False)

    snp_id = db.Column(db.Integer, db.ForeignKey(
        'snps.id'), nullable=False)

    data_set = db.relationship(
        'Dataset', backref=orm.backref('germline_gwas_results', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    feature = db.relationship(
        'Feature', backref=orm.backref('germline_gwas_results', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    snp = db.relationship(
        'Snp', backref=orm.backref('germline_gwas_results', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    def __repr__(self):
        return '<GermlineGwasResult %r>' % self.id
