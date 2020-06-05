from flaskr import db


class Node(db.Model):
    __tablename__ = 'nodes'
    id = db.Column(db.Integer, primary_key=True)

    gene_id = db.Column(db.Integer, db.ForeignKey('genes.id'), nullable=True)

    feature_id = db.Column(
        db.Integer, db.ForeignKey('features.id'), nullable=True)

    label = db.Column(db.String, nullable=True)
    score = db.Column(db.Numeric, nullable=True)
    x = db.Column(db.Numeric, nullable=True)
    y = db.Column(db.Numeric, nullable=True)

    def __repr__(self):
        return '<Node %r>' % self.id
