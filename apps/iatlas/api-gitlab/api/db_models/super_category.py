from api import db
from . import Base


class SuperCategory(Base):
    __tablename__ = 'super_categories'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<SuperCategory %r>' % self.name
