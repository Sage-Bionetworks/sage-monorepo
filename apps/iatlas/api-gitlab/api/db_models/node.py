from sqlalchemy import orm
from api import db
from . import Base


class Node(Base):
    __tablename__ = 'nodes'
    id = db.Column(db.String, primary_key=True)
    label = db.Column(db.String, nullable=True)
    network = db.Column(db.String, nullable=False)
    name = db.Column(db.String, nullable=False)
    score = db.Column(db.Numeric, nullable=True)
    x = db.Column(db.Numeric, nullable=True)
    y = db.Column(db.Numeric, nullable=True)

    dataset_id = db.Column(
        db.String, db.ForeignKey('datasets.id'), nullable=True)

    node_feature_id = db.Column(
        db.String, db.ForeignKey('features.id'), nullable=True)

    node_gene_id = db.Column(db.String, db.ForeignKey('genes.id'), nullable=True)

    tag_1_id = db.Column(
        db.String, db.ForeignKey('tags.id'), nullable=False)

    tag_2_id = db.Column(
        db.String, db.ForeignKey('tags.id'), nullable=True)


    data_set = db.relationship(
        'Dataset', backref=orm.backref('node', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    feature = db.relationship(
        'Feature', backref=orm.backref('node', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    gene = db.relationship(
        'Gene', backref=orm.backref('node', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    tag1 = db.relationship(
        'Tag',
        backref=orm.backref('node1', uselist=True, lazy='noload'),
        uselist=False,
        lazy='noload',
        foreign_keys=tag_1_id
    )

    tag2 = db.relationship(
        'Tag',
        backref=orm.backref('node2', uselist=True, lazy='noload'),
        uselist=False,
        lazy='noload',
        foreign_keys=tag_2_id
    )



    def __repr__(self):
        return '<Node %r>' % self.id
