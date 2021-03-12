from sqlalchemy import orm
from api import db
from . import Base
from api.enums import status_enum


class SampleToMutation(Base):
    __tablename__ = 'samples_to_mutations'

    sample_id = db.Column(
        db.Integer, db.ForeignKey('samples.id'), primary_key=True)

    mutation_id = db.Column(
        db.Integer, db.ForeignKey('mutations.id'), primary_key=True)

    status = db.Column(status_enum, nullable=True)

    samples = db.relationship('Sample', backref=orm.backref(
        'sample_mutation_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    mutations = db.relationship('Mutation', backref=orm.backref(
        'sample_mutation_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<SampleToMutation %r>' % self.sample_id
