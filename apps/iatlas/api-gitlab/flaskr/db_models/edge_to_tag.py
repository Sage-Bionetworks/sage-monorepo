from flaskr import db


class EdgeToTag(db.Model):
    __tablename__ = 'edges_to_tags'

    edge_id = db.Column(
        db.Integer, db.ForeignKey('edge.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tag.id'), nullable=False)

    # def __repr__(self):
    #     return '<EdgeToTag %r>' % self.edge_id
