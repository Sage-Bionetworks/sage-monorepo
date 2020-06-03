from flaskr import db


class GeneType(db.Model):
    __tablename__ = 'gene_types'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)
    display = db.Column(db.String, nullable=True)

    def __repr__(self):
        return '<GeneType %r>' % self.name
