from api import db
from . import Base


class Publication(Base):
    __tablename__ = 'publications'
    id = db.Column(db.Integer, primary_key=True)
    first_author_last_name = db.Column(db.String, nullable=True)
    journal = db.Column(db.String, nullable=True)
    pubmed_id = db.Column(db.Integer, nullable=False)
    title = db.Column(db.String, nullable=True)
    year = db.Column(db.Integer, nullable=True)

    genes = db.relationship(
        "Gene", secondary='publications_to_genes', uselist=True, lazy='noload')

    def __repr__(self):
        return '<Publication %r>' % self.pubmed_id
