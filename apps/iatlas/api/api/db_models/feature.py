from sqlalchemy import orm
from api import db
from . import Base
from api.enums import unit_enum


class Feature(Base):
    __tablename__ = "features"
    id = db.Column(db.String, primary_key=True)
    name = db.Column(db.String, nullable=False)
    display = db.Column(db.String, nullable=True)
    order = db.Column(db.Integer, nullable=True)
    unit = db.Column(unit_enum, nullable=True)
    germline_category = db.Column(db.String, nullable=True)
    germline_module = db.Column(db.String, nullable=True)
    feature_class = db.Column(db.String, nullable=False)
    method_tag = db.Column(db.String, nullable=False)

    samples = db.relationship(
        "Sample", secondary="features_to_samples", uselist=True, lazy="noload"
    )

    def __repr__(self):
        return "<Feature %r>" % self.name
