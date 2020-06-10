from flaskr import db
from . import Base


class GeneToType(Base):
    __tablename__ = 'genes_to_types'

    gene_id = db.Column(
        db.Integer, db.ForeignKey('genes.id'), primary_key=True)

    type_id = db.Column(
        db.Integer, db.ForeignKey('gene_types.id'), nullable=False)

    def __repr__(self):
        return '<GeneToType %r>' % self.gene_id
