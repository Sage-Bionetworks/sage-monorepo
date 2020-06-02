from flaskr import db


class TagToTag(db.Model):
    __tablename__ = 'tags_to_tags'

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tag.id'), primary_key=True)

    related_tag_id = db.Column(
        db.Integer, db.ForeignKey('tag.id'), nullable=False)

    def __repr__(self):
        return '<TagToTag %r>' % self.tag_id
