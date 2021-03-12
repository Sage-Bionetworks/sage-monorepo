from sqlalchemy import orm
from api import db
from . import Base


class TagToPublication(Base):
    __tablename__ = 'tags_to_publications'

    publication_id = db.Column(
        db.Integer, db.ForeignKey('publications.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), primary_key=True)

    publications = db.relationship('Publication', backref=orm.backref(
        'tag_publication_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    tags = db.relationship('Tag', backref=orm.backref(
        'tag_publication_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<TagToPublication %r>' % self.tag_id
