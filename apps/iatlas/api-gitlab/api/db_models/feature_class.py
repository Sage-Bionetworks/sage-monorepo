from api import db
from . import Base


class FeatureClass(Base):
    __tablename__ = 'classes'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<FeatureClass %r>' % self.name
