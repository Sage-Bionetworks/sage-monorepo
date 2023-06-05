from sqlalchemy import orm
from api import db
from . import Base


class PublicationToGeneToGeneSet(Base):
    __tablename__ = 'publications_to_genes_to_gene_sets'

    gene_id = db.Column(
        db.String, db.ForeignKey('genes.id'), primary_key=True)

    gene_set_id = db.Column(
        db.String, db.ForeignKey('gene_sets.id'), primary_key=True)

    publication_id = db.Column(
        db.String, db.ForeignKey('publications.id'), primary_key=True)

    genes = db.relationship('Gene', backref=orm.backref(
        'publication_gene_gene_set_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    gene_sets = db.relationship('GeneSet', backref=orm.backref(
        'publication_gene_gene_set_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    publications = db.relationship('Publication', backref=orm.backref(
        'publication_gene_gene_set_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<PublicationToGeneToGeneSet %r>' % self.gene_id
