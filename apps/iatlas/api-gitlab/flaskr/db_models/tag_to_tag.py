from sqlalchemy import orm
from flaskr import db
from . import Base


class TagToTag(Base):
    __tablename__ = 'tags_to_tags'

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), primary_key=True)

    related_tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), primary_key=True)

    tags = db.relationship('Tag', backref=orm.backref("tag_related_assoc"), uselist=True,
                           primaryjoin="Tag.id==TagToTag.tag_id", lazy='noload')

    related_tags = db.relationship('Tag', backref=orm.backref("related_tag_assoc"), uselist=True,
                                   primaryjoin="Tag.id==TagToTag.related_tag_id", lazy='noload')

    def __repr__(self):
        return '<TagToTag %r>' % self.tag_id
