from sqlalchemy import orm
from api import db
from . import Base
from api.enums import status_enum


class SampleToMutation(Base):
    __tablename__ = 'samples_to_mutations'
    id = db.Column(db.String, primary_key=True)
    mutation_status = db.Column(status_enum, nullable=False)

    sample_id = db.Column(
        db.String, db.ForeignKey('samples.id'), primary_key=True)

    mutation_id = db.Column(
        db.String, db.ForeignKey('mutations.id'), primary_key=True)

    samples = db.relationship('Sample', backref=orm.backref(
        'sample_mutation_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    mutations = db.relationship('Mutation', backref=orm.backref(
        'sample_mutation_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<SampleToMutation %r>' % self.sample_id
