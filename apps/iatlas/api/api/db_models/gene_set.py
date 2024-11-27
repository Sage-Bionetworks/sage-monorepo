from api import db
from . import Base


class GeneSet(Base):
    __tablename__ = "gene_sets"
    id = db.Column(db.String, primary_key=True)
    name = db.Column(db.String, nullable=False)
    display = db.Column(db.String, nullable=True)

    genes = db.relationship(
        "Gene", secondary="genes_to_gene_sets", uselist=True, lazy="noload"
    )

    publications = db.relationship(
        "Publication",
        secondary="publications_to_genes_to_gene_sets",
        uselist=True,
        lazy="noload",
    )

    def __repr__(self):
        return "<GeneSet %r>" % self.name
