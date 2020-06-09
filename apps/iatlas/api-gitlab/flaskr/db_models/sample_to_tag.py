from flaskr import db
from . import Base


class SampleToTag(Base):
    __tablename__ = 'samples_to_tags'

    sample_id = db.Column(
        db.Integer, db.ForeignKey('samples.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), nullable=False)

    sample = db.relationship('Sample', uselist=False)
    tag = db.relationship('Tag', uselist=False)

    def __repr__(self):
        return '<SampleToTag %r>' % self.sample_id
