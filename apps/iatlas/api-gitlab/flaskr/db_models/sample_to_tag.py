from flaskr import db


class SampleToTag(db.Model):
    __tablename__ = 'samples_to_tags'

    sample_id = db.Column(
        db.Integer, db.ForeignKey('sample.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tag.id'), nullable=False)

    def __repr__(self):
        return '<SampleToTag %r>' % self.sample_id
