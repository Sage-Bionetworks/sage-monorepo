from sqlalchemy import orm
from flaskr import db
from . import Base


class GeneToType(Base):
    __tablename__ = 'genes_to_types'

    gene_id = db.Column(
        db.Integer, db.ForeignKey('genes.id'), primary_key=True)

    type_id = db.Column(
        db.Integer, db.ForeignKey('gene_types.id'), nullable=False)

    genes = db.relationship('Gene', backref=orm.backref(
        "gene_type_assoc"), uselist=True, lazy='noload')

    types = db.relationship('GeneType', backref=orm.backref(
        "gene_type_assoc"), uselist=True, lazy='noload')

    def __repr__(self):
        return '<GeneToType %r>' % self.gene_id
