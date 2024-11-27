from sqlalchemy import orm
from api import db
from . import Base


class Cell(Base):
    __tablename__ = "cells"
    id = db.Column(db.String, primary_key=True)
    name = db.Column(db.String, nullable=False)
    cell_type = db.Column(db.String, nullable=False)

    def __repr__(self):
        return "<Cell %r>" % self.name
