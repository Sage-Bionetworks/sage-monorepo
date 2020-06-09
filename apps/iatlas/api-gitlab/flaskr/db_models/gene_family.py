from flaskr import db
from . import Base


class GeneFamily(Base):
    __tablename__ = 'gene_families'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<GeneFamily %r>' % self.name
