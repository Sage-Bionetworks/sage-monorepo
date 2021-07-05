from sqlalchemy import orm
from api import db
from . import Base


class CohortToSample(Base):
    __tablename__ = 'cohorts_to_samples'

    id = db.Column(db.Integer, primary_key=True)

    cohort_id = db.Column(db.Integer, db.ForeignKey(
        'cohorts.id'), primary_key=True)

    sample_id = db.Column(db.Integer, db.ForeignKey(
        'samples.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), nullable=True)

    cohort = db.relationship('Cohort', backref=orm.backref(
        'cohort_sample_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    sample = db.relationship('Sample', backref=orm.backref(
        'cohort_sample_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    tag = db.relationship(
        'Tag', backref=orm.backref('cohorts_to_samples', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    def __repr__(self):
        return '<CohortToSample %r>' % self.id
