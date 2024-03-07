from sqlalchemy import orm
from api import db
from . import Base


class CellToGene(Base):
    __tablename__ = 'cells_to_genes'

    id = db.Column(db.String, primary_key=True)
    single_cell_seq = db.Column(db.Numeric, nullable=False)

    gene_id = db.Column(db.String, db.ForeignKey(
        'genes.id'), primary_key=True)

    cell_id = db.Column(db.String, db.ForeignKey(
        'cells.id'), primary_key=True)

    gene = db.relationship('Gene', backref=orm.backref(
        'gene_cell_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    cell = db.relationship('Cell', backref=orm.backref(
        'gene_cell_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    def __repr__(self):
        return '<CellToGene %r>' % self.id