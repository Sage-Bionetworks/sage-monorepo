from sqlalchemy import orm
from api import db
from . import Base


class Edge(Base):
    __tablename__ = 'edges'
    id = db.Column(db.String, primary_key=True)

    node_1_id = db.Column(
        db.String, db.ForeignKey('nodes.id'), nullable=False)

    node_2_id = db.Column(
        db.String, db.ForeignKey('nodes.id'), nullable=False)

    label = db.Column(db.String, nullable=True)
    name = db.Column(db.String, nullable=False)
    score = db.Column(db.Numeric, nullable=True)

    node_1 = db.relationship(
        'Node', backref=orm.backref('edges_primary', uselist=True, lazy='noload'),
        uselist=False, lazy='noload', primaryjoin='Node.id==Edge.node_1_id')

    node_2 = db.relationship(
        'Node', backref=orm.backref('edges_secondary', uselist=True, lazy='noload'),
        uselist=False, lazy='noload', primaryjoin='Node.id==Edge.node_2_id')

    def __repr__(self):
        return '<Edge %r>' % self.id
