from sqlalchemy import orm
from api import db
from . import Base


class Snp(Base):
    __tablename__ = 'snps'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)
    rsid = db.Column(db.String, nullable=True)
    chr = db.Column(db.String, nullable=True)
    bp = db.Column(db.Integer, nullable=True)

    def __repr__(self):
        return '<Snp %r>' % self.name
