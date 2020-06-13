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
        "Tag", foreign_keys='TagToTag.related_tag_id', lazy='noload',
        secondary='tags_to_tags', back_populates="related_tags", uselist=True)

    related_tags = db.relationship(
        "Tag", foreign_keys='TagToTag.tag_id', lazy='noload',
        secondary='tags_to_tags', back_populates="tags", uselist=True)

    samples = db.relationship("Sample", lazy='noload',
                              secondary='samples_to_tags')

    def __repr__(self):
        return '<Tag %r>' % self.name
