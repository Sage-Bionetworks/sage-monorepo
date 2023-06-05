from api import db
from . import Base


class MutationCode(Base):
    __tablename__ = 'mutation_codes'
    id = db.Column(db.String, primary_key=True)
    code = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<MutationCode %r>' % self.code
