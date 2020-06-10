from sqlalchemy import orm
from flaskr import db
from . import Base
from flaskr.enums import status_enum


class SampleToMutation(Base):
    __tablename__ = 'samples_to_mutations'

    sample_id = db.Column(
        db.Integer, db.ForeignKey('samples.id'), primary_key=True)

    mutation_id = db.Column(
        db.Integer, db.ForeignKey('mutations.id'), nullable=False)

    status = db.Column(status_enum, nullable=True)

    samples = db.relationship('Sample', backref=orm.backref(
        "sample_mutation_assoc"), uselist=True)

    mutations = db.relationship('Mutation', backref=orm.backref(
        "sample_mutation_assoc"), uselist=True)

    def __repr__(self):
        return '<SampleToMutation %r>' % self.sample_id
