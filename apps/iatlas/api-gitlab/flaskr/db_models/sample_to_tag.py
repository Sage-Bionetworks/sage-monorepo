from flaskr import db


class SampleToTag(db.Model):
    __tablename__ = 'samples_to_tags'

    sample_id = db.Column(
        db.Integer, db.ForeignKey('sample.id'), nullable=False)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tag.id'), primary_key=True)

    def __repr__(self):
        return '<SampleToTag %r>' % self.sample_id
