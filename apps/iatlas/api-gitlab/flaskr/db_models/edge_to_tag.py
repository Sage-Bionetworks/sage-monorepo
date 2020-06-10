from flaskr import db
from . import Base


class EdgeToTag(Base):
    __tablename__ = 'edges_to_tags'

    edge_id = db.Column(
        db.Integer, db.ForeignKey('edges.id'), primary_key=True)

    tag_id = db.Column(
        db.Integer, db.ForeignKey('tags.id'), nullable=False)

    # def __repr__(self):
    #     return '<EdgeToTag %r>' % self.edge_id
