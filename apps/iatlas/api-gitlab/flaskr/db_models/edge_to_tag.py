from flaskr import db


class EdgeToTag(db.Model):
    __tablename__ = 'edges_to_tags'

    edge_id = db.Column(
        db.Integer, db.ForeignKey('edges.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), nullable=False)

    # def __repr__(self):
    #     return '<EdgeToTag %r>' % self.edge_id
