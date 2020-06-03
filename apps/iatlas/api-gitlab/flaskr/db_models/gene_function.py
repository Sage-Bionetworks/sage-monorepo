from flaskr import db


class GeneFunction(db.Model):
    __tablename__ = 'gene_functions'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<GeneFunction %r>' % self.name
