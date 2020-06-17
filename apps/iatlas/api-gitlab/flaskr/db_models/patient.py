from sqlalchemy import orm
from flaskr import db
from . import Base


class Patient(Base):
    __tablename__ = 'patients'
    id = db.Column(db.Integer, primary_key=True)
    age = db.Column(db.Integer, nullable=True)
    barcode = db.Column(db.String, nullable=False)
    ethnicity = db.Column(db.String, nullable=True)
    gender = db.Column(db.String, nullable=True)
    height = db.Column(db.Integer, nullable=True)
    race = db.Column(db.String, nullable=True)
    weight = db.Column(db.Integer, nullable=True)

    def __repr__(self):
        return '<Patient %r>' % self.barcode
