from flaskr import db


class TherapyType(db.Model):
    __tablename__ = 'therapy_types'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String)

    def __repr__(self):
        return '<TherapyType %r>' % self.name
