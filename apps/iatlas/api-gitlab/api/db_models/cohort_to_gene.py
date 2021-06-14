from sqlalchemy import orm
from api import db
from . import Base


class CohortToGene(Base):
    __tablename__ = 'cohorts_to_genes'

    id = db.Column(db.Integer, primary_key=True)

    cohort_id = db.Column(db.Integer, db.ForeignKey(
        'cohorts.id'), primary_key=True)

    gene_id = db.Column(db.Integer, db.ForeignKey(
        'genes.id'), primary_key=True)

    cohort = db.relationship('Cohort', backref=orm.backref(
        'cohort_gene_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    gene = db.relationship('Gene', backref=orm.backref(
        'cohort_gene_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    def __repr__(self):
        return '<CohortToGene %r>' % self.id
