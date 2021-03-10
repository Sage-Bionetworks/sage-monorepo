from sqlalchemy import orm
from api import db
from . import Base
from api.enums import qtl_enum, ecaviar_pp_enum, coloc_plot_type_enum


class Colocalization(Base):
    __tablename__ = 'colocalizations'
    id = db.Column(db.Integer, primary_key=True)
    qtl_type = db.Column(qtl_enum, nullable=False)
    ecaviar_pp = db.Column(ecaviar_pp_enum, nullable=True)
    plot_type = db.Column(coloc_plot_type_enum, nullable=True)
    tissue = db.Column(db.String, nullable=True)
    splice_loc = db.Column(db.String, nullable=True)
    plot_link = db.Column(db.String, nullable=False)

    dataset_id = db.Column(db.Integer, db.ForeignKey(
        'datasets.id'), nullable=False)

    coloc_dataset_id = db.Column(db.Integer, db.ForeignKey(
        'datasets.id'), nullable=False)

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'features.id'), nullable=False)

    gene_id = db.Column(db.Integer, db.ForeignKey('genes.id'), nullable=False)

    snp_id = db.Column(db.Integer, db.ForeignKey('snps.id'), nullable=False)

    data_set = db.relationship(
        'Dataset', backref=orm.backref('colocalizations_primary', uselist=True, lazy='noload'),
        uselist=False, lazy='noload', primaryjoin='Dataset.id==Colocalization.dataset_id')

    coloc_data_set = db.relationship(
        'Dataset', backref=orm.backref('colocalizations_secondary', uselist=True, lazy='noload'),
        uselist=False, lazy='noload', primaryjoin='Dataset.id==Colocalization.coloc_dataset_id')

    feature = db.relationship(
        'Feature', backref=orm.backref('colocalizations', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    gene = db.relationship(
        'Gene', backref=orm.backref('colocalizations', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    snp = db.relationship(
        'Snp', backref=orm.backref('colocalizations', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    def __repr__(self):
        return '<Colocalization %r>' % self.id
