from flaskr import db


class NodeToTag(db.Model):
    __tablename__ = 'nodes_to_tags'

    node_id = db.Column(
        db.Integer, db.ForeignKey('node.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tag.id'), nullable=False)

    def __repr__(self):
        return '<NodeToTag %r>' % self.node_id
