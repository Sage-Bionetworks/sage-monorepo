from sqlalchemy import orm
from api import db
from . import Base


class PublicationToGeneToGeneType(Base):
    __tablename__ = 'publications_to_genes_to_gene_types'

    gene_id = db.Column(
        db.Integer, db.ForeignKey('genes.id'), primary_key=True)

    gene_type_id = db.Column(
        db.Integer, db.ForeignKey('gene_types.id'), primary_key=True)

    publication_id = db.Column(
        db.Integer, db.ForeignKey('publications.id'), primary_key=True)

    genes = db.relationship('Gene', backref=orm.backref(
        'publication_gene_gene_type_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    gene_types = db.relationship('GeneType', backref=orm.backref(
        'publication_gene_gene_type_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    publications = db.relationship('Publication', backref=orm.backref(
        'publication_gene_gene_type_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<PublicationToGeneToGeneType %r>' % self.gene_id
