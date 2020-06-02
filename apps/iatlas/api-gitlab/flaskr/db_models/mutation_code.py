from flaskr import db


class MutationCode(db.Model):
    __tablename__ = 'mutation_codes'
    id = db.Column(db.Integer, primary_key=True)
    code = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<MutationCode %r>' % self.code
