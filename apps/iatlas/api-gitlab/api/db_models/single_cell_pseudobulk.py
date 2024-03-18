from sqlalchemy import orm
from api import db
from . import Base


class SingleCellPseudobulk(Base):
    __tablename__ = 'single_cell_pseudobulk'
    id = db.Column(db.String, primary_key=True)
    cell_type = db.Column(db.String, nullable=False)
    single_cell_seq_sum = db.Column(db.Numeric, nullable=False)

    gene_id = db.Column(db.String, db.ForeignKey(
        'genes.id'), primary_key=True)

    sample_id = db.Column(db.String, db.ForeignKey(
        'samples.id'), primary_key=True)

    gene = db.relationship('Gene', backref=orm.backref(
        'pseudobulk_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    sample = db.relationship('Sample', backref=orm.backref(
        'pseudobulk_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    def __repr__(self):
        return '<SingleCellPseudobulk %r>' % self.gene_id