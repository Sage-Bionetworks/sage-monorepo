from sqlalchemy import orm
from api import db
from . import Base


class CellToFeature(Base):
    __tablename__ = "cells_to_features"

    id = db.Column(db.String, primary_key=True)
    feature_value = db.Column(db.Numeric, nullable=False)

    feature_id = db.Column(db.String, db.ForeignKey("features.id"), primary_key=True)

    cell_id = db.Column(db.String, db.ForeignKey("cells.id"), primary_key=True)

    feature = db.relationship(
        "Feature",
        backref=orm.backref("feature_cell_assoc", uselist=True, lazy="noload"),
        uselist=False,
        lazy="noload",
    )

    cell = db.relationship(
        "Cell",
        backref=orm.backref("feature_cell_assoc", uselist=True, lazy="noload"),
        uselist=False,
        lazy="noload",
    )

    def __repr__(self):
        return "<CellToFeature %r>" % self.id
