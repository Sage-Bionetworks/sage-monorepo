from sqlalchemy import orm
from api import db
from . import Base


class SampleToTag(Base):
    __tablename__ = 'samples_to_tags2'

    sample_id = db.Column(
        db.Integer, db.ForeignKey('samples.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), primary_key=True)

    samples = db.relationship('Sample', backref=orm.backref(
        'sample_tag_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    tags = db.relationship('Tag', backref=orm.backref(
        'sample_tag_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<SampleToTag %r>' % self.sample_id
