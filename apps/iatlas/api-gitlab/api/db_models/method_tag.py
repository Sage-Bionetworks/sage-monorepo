from api import db
from . import Base


class MethodTag(Base):
    __tablename__ = 'method_tags'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<MethodTag %r>' % self.name
