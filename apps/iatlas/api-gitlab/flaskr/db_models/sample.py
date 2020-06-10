from flaskr import db
from . import Base


class Sample(Base):
    __tablename__ = 'samples'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    patient_id = db.Column(
        db.Integer, db.ForeignKey('patients.id'), nullable=True)

    mutations = db.relationship("Mutation", secondary='samples_to_mutations')
    tags = db.relationship("Tag", secondary='samples_to_tags')

    def __repr__(self):
        return '<Sample %r>' % self.name
