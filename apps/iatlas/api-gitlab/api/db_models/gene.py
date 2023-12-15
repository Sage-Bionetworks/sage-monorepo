from sqlalchemy import orm
from api import db
from . import Base


class Gene(Base):
    __tablename__ = 'genes'
    id = db.Column(db.String, primary_key=True)
    entrez_id = db.Column(db.Integer, nullable=False)
    hgnc_id = db.Column(db.String, nullable=False)
    description = db.Column(db.String, nullable=True)
    friendly_name = db.Column(db.String, nullable=True)
    io_landscape_name = db.Column(db.String, nullable=True)
    gene_family = db.Column(db.String, nullable=True)
    gene_function = db.Column(db.String, nullable=True)
    immune_checkpoint = db.Column(db.String, nullable=True)
    gene_pathway = db.Column(db.String, nullable=True)
    super_category = db.Column(db.String, nullable=True)
    therapy_type = db.Column(db.String, nullable=True)

    gene_sets = db.relationship(
        "GeneSet", secondary='genes_to_gene_sets', uselist=True, lazy='noload')

    publications = db.relationship(
        "Publication", secondary='publications_to_genes_to_gene_sets', uselist=True, lazy='noload')

    samples = db.relationship(
        "Sample", secondary='genes_to_samples', uselist=True, lazy='noload')

    def __repr__(self):
        return '<Gene %r>' % self.entrez_id
