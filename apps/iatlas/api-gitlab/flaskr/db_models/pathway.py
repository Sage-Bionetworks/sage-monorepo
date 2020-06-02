from flaskr import db


class Pathway(db.Model):
    __tablename__ = 'pathways'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String)

    def __repr__(self):
        return '<Pathway %r>' % self.name
