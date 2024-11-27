from api import db
from . import Base


class Publication(Base):
    __tablename__ = "publications"
    id = db.Column(db.String, primary_key=True)
    do_id = db.Column(db.String, nullable=True)
    first_author_last_name = db.Column(db.String, nullable=True)
    journal = db.Column(db.String, nullable=True)
    link = db.Column(db.String, nullable=False)
    pubmed_id = db.Column(db.Integer, nullable=True)
    title = db.Column(db.String, nullable=True)
    year = db.Column(db.Integer, nullable=True)

    genes = db.relationship(
        "Gene",
        secondary="publications_to_genes_to_gene_sets",
        uselist=True,
        lazy="noload",
    )

    gene_sets = db.relationship(
        "GeneSet",
        secondary="publications_to_genes_to_gene_sets",
        uselist=True,
        lazy="noload",
    )

    tags = db.relationship(
        "Tag", secondary="tags_to_publications", uselist=True, lazy="noload"
    )

    def __repr__(self):
        return "<Publication %r>" % self.title
