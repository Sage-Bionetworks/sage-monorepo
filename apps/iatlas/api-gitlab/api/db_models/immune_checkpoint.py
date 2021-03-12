from api import db
from . import Base


class ImmuneCheckpoint(Base):
    __tablename__ = 'immune_checkpoints'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<ImmuneCheckpoint %r>' % self.name
