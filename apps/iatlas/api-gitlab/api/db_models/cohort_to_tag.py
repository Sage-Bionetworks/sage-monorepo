from sqlalchemy import orm
from api import db
from . import Base


class CohortToTag(Base):
    __tablename__ = 'cohorts_to_tags'

    id = db.Column(db.Integer, primary_key=True)

    cohort_id = db.Column(db.Integer, db.ForeignKey(
        'cohorts.id'), primary_key=True)

    tag_id = db.Column(db.Integer, db.ForeignKey(
        'tags.id'), primary_key=True)

    cohort = db.relationship('Cohort', backref=orm.backref(
        'cohort_tag_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    tag = db.relationship('Tag', backref=orm.backref(
        'cohort_tag_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    def __repr__(self):
        return '<CohortToTag %r>' % self.id
