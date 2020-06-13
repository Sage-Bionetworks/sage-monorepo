from flaskr import db
from . import Base


class GeneType(Base):
    __tablename__ = 'gene_types'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)
    display = db.Column(db.String, nullable=True)

    genes = db.relationship(
        "Gene", secondary='genes_to_types', uselist=True, lazy='noload')

    def __repr__(self):
        return '<GeneType %r>' % self.name
