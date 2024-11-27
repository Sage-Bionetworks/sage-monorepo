from sqlalchemy import orm
from api import db
from . import Base


class GeneToGeneSet(Base):
    __tablename__ = "genes_to_gene_sets"
    id = db.Column(db.String, primary_key=True)

    gene_id = db.Column(db.String, db.ForeignKey("genes.id"), primary_key=True)

    gene_set_id = db.Column(db.String, db.ForeignKey("gene_sets.id"), primary_key=True)

    genes = db.relationship(
        "Gene",
        backref=orm.backref("gene_set_assoc", uselist=True, lazy="noload"),
        uselist=True,
        lazy="noload",
    )

    gene_sets = db.relationship(
        "GeneSet",
        backref=orm.backref("gene_set_assoc", uselist=True, lazy="noload"),
        uselist=True,
        lazy="noload",
    )

    def __repr__(self):
        return "<GeneToGeneSet %r>" % self.gene_id
