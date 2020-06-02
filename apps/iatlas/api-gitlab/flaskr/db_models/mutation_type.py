from flaskr import db


class MutationType(db.Model):
    __tablename__ = 'mutation_types'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)
    display = db.Column(db.String, nullable=True)

    def __repr__(self):
        return '<MutationType %r>' % self.name
