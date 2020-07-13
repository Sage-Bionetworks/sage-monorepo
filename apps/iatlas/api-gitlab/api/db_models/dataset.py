from api import db
from . import Base


class Dataset(Base):
    __tablename__ = 'datasets'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)
    display = db.Column(db.String, nullable=True)

    samples = db.relationship(
        "Sample", secondary='datasets_to_samples', uselist=True, lazy='noload')

    tags = db.relationship(
        "Tag", secondary='datasets_to_tags', uselist=True, lazy='noload')

    def __repr__(self):
        return '<Dataset %r>' % self.name
