from sqlalchemy import orm
from api import db
from . import Base


class CohortToMutation(Base):
    __tablename__ = 'cohorts_to_mutations'

    id = db.Column(db.Integer, primary_key=True)

    cohort_id = db.Column(db.Integer, db.ForeignKey(
        'cohorts.id'), primary_key=True)

    mutation_id = db.Column(db.Integer, db.ForeignKey(
        'mutations.id'), primary_key=True)

    cohort = db.relationship('Cohort', backref=orm.backref(
        'cohort_mutation_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    mutation = db.relationship('Mutation', backref=orm.backref(
        'cohort_mutation_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    def __repr__(self):
        return '<CohortToMutation %r>' % self.id
