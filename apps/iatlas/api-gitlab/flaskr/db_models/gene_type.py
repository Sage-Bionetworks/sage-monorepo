from flaskr import db


class GeneType(db.Model):
    __tablename__ = 'gene_types'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String)
    display = db.Column(db.String)

    def __repr__(self):
        return '<GeneType %r>' % self.name
