from sqlalchemy import orm
from api import db
from . import Base
from api.enums import ethnicity_enum, gender_enum, race_enum


class Patient(Base):
    __tablename__ = 'patients'
    id = db.Column(db.Integer, primary_key=True)
    age_at_diagnosis = db.Column(db.Integer, nullable=True)
    barcode = db.Column(db.String, nullable=False)
    ethnicity = db.Column(ethnicity_enum, nullable=True)
    gender = db.Column(gender_enum, nullable=True)
    height = db.Column(db.Integer, nullable=True)
    race = db.Column(race_enum, nullable=True)
    weight = db.Column(db.Integer, nullable=True)

    def __repr__(self):
        return '<Patient %r>' % self.barcode
