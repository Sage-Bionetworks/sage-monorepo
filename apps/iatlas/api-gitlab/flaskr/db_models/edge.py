from flaskr import db
from . import Base


class Edge(Base):
    __tablename__ = 'edges'
    id = db.Column(db.Integer, primary_key=True)

    node_1_id = db.Column(
        db.Integer, db.ForeignKey('nodes.id'), nullable=False)

    node_2_id = db.Column(
        db.Integer, db.ForeignKey('nodes.id'), nullable=False)

    label = db.Column(db.String, nullable=True)
    score = db.Column(db.Numeric, nullable=True)

    node_1 = db.relationship(
        'Node', backref='edges_primary', uselist=False, lazy='noload',
        primaryjoin='Node.id==Edge.node_1_id')

    node_2 = db.relationship(
        'Node', backref='edges_secondary', uselist=False, lazy='noload',
        primaryjoin='Node.id==Edge.node_2_id')

    def __repr__(self):
        return '<Edge %r>' % self.id
