from sqlalchemy import orm
from flaskr import db
from . import Base


class GeneToSample(Base):
    __tablename__ = 'genes_to_samples'

    gene_id = db.Column(db.Integer, db.ForeignKey(
        'genes.id'), primary_key=True)

    sample_id = db.Column(db.Integer, db.ForeignKey(
        'samples.id'), nullable=False)

    rna_seq_expr = db.Column(db.Float, nullable=True)

    genes = db.relationship('Gene', backref=orm.backref(
        "gene_sample_assoc"), uselist=True, lazy='noload')

    samples = db.relationship('Sample', backref=orm.backref(
        "gene_sample_assoc"), uselist=True, lazy='noload')

    def __repr__(self):
        return '<GeneToSample %r>' % self.gene_id
