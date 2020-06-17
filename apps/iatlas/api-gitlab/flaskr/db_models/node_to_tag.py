from sqlalchemy import orm
from flaskr import db
from . import Base


class NodeToTag(Base):
    __tablename__ = 'nodes_to_tags'

    node_id = db.Column(
        db.Integer, db.ForeignKey('nodes.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), nullable=False)

    nodes = db.relationship('Node', backref=orm.backref(
        'node_tag_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    tags = db.relationship('Tag', backref=orm.backref(
        'node_tag_assoc', uselist=True, lazy='noload'), uselist=True, lazy='noload')

    def __repr__(self):
        return '<NodeToTag %r>' % self.node_id
