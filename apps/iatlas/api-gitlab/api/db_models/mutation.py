from sqlalchemy import orm
from api import db
from . import Base


class Mutation(Base):
    __tablename__ = 'mutations'
    id = db.Column(db.String, primary_key=True)
    name = db.Column(db.String, nullable=False)
    mutation_code = db.Column(db.String, nullable=False)
    mutation_type = db.Column(db.String, nullable=False)

    gene_id = db.Column(db.Integer, db.ForeignKey('genes.id'), nullable=False)

    gene = db.relationship(
        "Gene", backref=orm.backref('mutations', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    samples = db.relationship(
        "Sample", secondary='samples_to_mutations', uselist=True, lazy='noload')

    def __repr__(self):
        return '<Mutation %r>' % self.id
