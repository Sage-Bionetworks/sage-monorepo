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

    def __repr__(self):
        return '<SampleToMutation %r>' % self.sample_id
