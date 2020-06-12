from flaskr import db
from . import Base


class Sample(Base):
    __tablename__ = 'samples'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    patient_id = db.Column(
        db.Integer, db.ForeignKey('patients.id'), nullable=True)

    features = db.relationship(
        "Feature", secondary='features_to_samples', uselist=True, lazy='noload')

    genes = db.relationship(
        "Gene", secondary='genes_to_samples', uselist=True, lazy='noload')

    mutations = db.relationship(
        "Mutation", secondary='samples_to_mutations', uselist=True, lazy='noload')

    tags = db.relationship(
        "Tag", secondary='samples_to_tags', uselist=True, lazy='noload')

    def __repr__(self):
        return '<Sample %r>' % self.name
