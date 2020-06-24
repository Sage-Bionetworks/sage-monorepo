from sqlalchemy import orm
from api import db
from . import Base


class Node(Base):
    __tablename__ = 'nodes'
    id = db.Column(db.Integer, primary_key=True)

    dataset_id = db.Column(
        db.Integer, db.ForeignKey('datasets.id'), nullable=True)

    feature_id = db.Column(
        db.Integer, db.ForeignKey('features.id'), nullable=True)

    gene_id = db.Column(db.Integer, db.ForeignKey('genes.id'), nullable=True)

    label = db.Column(db.String, nullable=True)
    name = db.Column(db.String, nullable=False)
    score = db.Column(db.Numeric, nullable=True)
    x = db.Column(db.Numeric, nullable=True)
    y = db.Column(db.Numeric, nullable=True)

    dataset = db.relationship(
        'Dataset', backref=orm.backref('node', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    feature = db.relationship(
        'Feature', backref=orm.backref('node', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    gene = db.relationship(
        'Gene', backref=orm.backref('node', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    tags = db.relationship(
        "Tag", secondary='nodes_to_tags', uselist=True, lazy='noload')

    def __repr__(self):
        return '<Node %r>' % self.id
