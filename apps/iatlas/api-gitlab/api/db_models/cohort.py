from sqlalchemy import orm
from api import db
from . import Base


class Cohort(Base):
    __tablename__ = 'cohorts'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)
    clinical = db.Column(db.String, nullable=True)

    dataset_id = db.Column(db.Integer, db.ForeignKey(
        'datasets.id'), nullable=False)

    tag_id = db.Column(db.Integer, db.ForeignKey('tags.id'), nullable=False)

    data_set = db.relationship(
        'Dataset', backref=orm.backref('cohorts', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    tag = db.relationship(
        'Tag', backref=orm.backref('cohorts', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    samples = db.relationship(
        "Sample", secondary='cohorts_to_samples', uselist=True, lazy='noload')

    features = db.relationship(
        "Feature", secondary='cohorts_to_features', uselist=True, lazy='noload')

    genes = db.relationship(
        "Gene", secondary='cohorts_to_genes', uselist=True, lazy='noload')

    mutations = db.relationship(
        "Mutation", secondary='cohorts_to_mutations', uselist=True, lazy='noload')

    def __repr__(self):
        return '<Cohort %r>' % self.name
