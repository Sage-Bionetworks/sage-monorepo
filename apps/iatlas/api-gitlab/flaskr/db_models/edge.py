from flaskr import db


class Edge(db.Model):
    __tablename__ = 'edges'
    id = db.Column(db.Integer, primary_key=True)

    node_1_id = db.Column(
        db.Integer, db.ForeignKey('nodes.id'), nullable=False)

    node_2_id = db.Column(
        db.Integer, db.ForeignKey('nodes.id'), nullable=False)

    label = db.Column(db.String, nullable=True)
    score = db.Column(db.Numeric, nullable=True)

    def __repr__(self):
        return '<Edge %r>' % self.id
