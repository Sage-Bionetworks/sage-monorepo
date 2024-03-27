from sqlalchemy import orm
from api import db
from . import Base


class CohortToSample(Base):
    __tablename__ = 'cohorts_to_samples'

    id = db.Column(db.String, primary_key=True)

    cohort_id = db.Column(db.String, db.ForeignKey(
        'cohorts.id'), primary_key=True)

    sample_id = db.Column(db.String, db.ForeignKey(
        'samples.id'), primary_key=True)

    cohorts_to_samples_tag_id = db.Column(db.Integer, db.ForeignKey(
        'tags.id'), primary_key=True)

    cohort = db.relationship('Cohort', backref=orm.backref(
        'cohort_sample_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    sample = db.relationship('Sample', backref=orm.backref(
        'cohort_sample_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    def __repr__(self):
        return '<CohortToSample %r>' % self.id
