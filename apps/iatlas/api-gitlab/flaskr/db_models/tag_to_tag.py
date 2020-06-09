from flaskr import db
from . import Base


class TagToTag(Base):
    __tablename__ = 'tags_to_tags'

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), primary_key=True)

    related_tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), primary_key=True)

    tag = db.relationship('Tag',
                          back_populates="related_tags", uselist=False, primaryjoin="Tag.id==TagToTag.tag_id")
    related_tag = db.relationship(
        'Tag', back_populates="tags", uselist=False, primaryjoin="Tag.id==TagToTag.related_tag_id")

    def __repr__(self):
        return '<TagToTag %r>' % self.tag_id
