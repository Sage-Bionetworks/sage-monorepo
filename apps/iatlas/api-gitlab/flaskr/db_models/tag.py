from flaskr import db
from . import Base


class Tag(Base):
    __tablename__ = 'tags'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)
    characteristics = db.Column(db.String, nullable=True)
    display = db.Column(db.String, nullable=True)
    color = db.Column(db.String, nullable=True)

    tags = db.relationship(
        "TagToTag", foreign_keys='TagToTag.related_tag_id', back_populates="related_tag")
    related_tags = db.relationship(
        "TagToTag", foreign_keys='TagToTag.tag_id', back_populates="tag")

    def __repr__(self):
        return '<Tag %r>' % self.name
