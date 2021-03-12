from api import db
from . import Base


class GeneFunction(Base):
    __tablename__ = 'gene_functions'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<GeneFunction %r>' % self.name
