from sqlalchemy import orm
from api import db
from . import Base


class DatasetToTag(Base):
    __tablename__ = 'datasets_to_tags'

    dataset_id = db.Column(
        db.Integer, db.ForeignKey('datasets.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), nullable=False)

    data_sets = db.relationship('Dataset', backref=orm.backref(
        'dataset_tag_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    tags = db.relationship('Tag', backref=orm.backref(
        'dataset_tag_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<DatasetToTag %r>' % self.dataset_id
