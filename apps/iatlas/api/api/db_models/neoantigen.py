from sqlalchemy import orm
from api import db
from . import Base


class Neoantigen(Base):
    __tablename__ = "neoantigens"
    id = db.Column(db.String, primary_key=True)
    tpm = db.Column(db.Float, nullable=True)
    pmhc = db.Column(db.String, nullable=False)
    freq_pmhc = db.Column(db.Integer, nullable=False)
    patient_id = db.Column(db.String, db.ForeignKey("patients.id"), nullable=False)
    neoantigen_gene_id = db.Column(db.String, db.ForeignKey("genes.id"), nullable=True)

    gene = db.relationship(
        "Gene",
        backref=orm.backref("neoantigen_assoc", uselist=True, lazy="noload"),
        uselist=True,
        lazy="noload",
    )

    patient = db.relationship(
        "Patient",
        backref=orm.backref("neoantigen_assoc", uselist=True, lazy="noload"),
        uselist=True,
        lazy="noload",
    )

    def __repr__(self):
        return "<Neoantigen %r>" % self.id
