from sqlalchemy import orm
from api import db
from . import Base


class CellToSample(Base):
    __tablename__ = "cells_to_samples"

    id = db.Column(db.String, primary_key=True)

    sample_id = db.Column(db.String, db.ForeignKey("samples.id"), primary_key=True)

    cell_id = db.Column(db.String, db.ForeignKey("cells.id"), primary_key=True)

    sample = db.relationship(
        "Sample",
        backref=orm.backref("sample_cell_assoc", uselist=True, lazy="noload"),
        uselist=False,
        lazy="noload",
    )

    cell = db.relationship(
        "Cell",
        backref=orm.backref("sample_cell_assoc", uselist=True, lazy="noload"),
        uselist=False,
        lazy="noload",
    )

    def __repr__(self):
        return "<CellToSample %r>" % self.id
