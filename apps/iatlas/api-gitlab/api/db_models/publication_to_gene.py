from sqlalchemy import orm
from api import db
from . import Base


class PublicationToGene(Base):
    __tablename__ = 'publications_to_genes'

    gene_id = db.Column(
        db.Integer, db.ForeignKey('genes.id'), primary_key=True)

    publication_id = db.Column(
        db.Integer, db.ForeignKey('publications.id'), primary_key=True)

    genes = db.relationship('Gene', backref=orm.backref(
        'publication_gene_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    publications = db.relationship('Publication', backref=orm.backref(
        'publication_gene_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<PublicationToGene %r>' % self.gene_id
