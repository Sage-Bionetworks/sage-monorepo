from flaskr import db


class Slide(db.Model):
    __tablename__ = 'slides'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String)
    description = db.Column(db.String)
    patient_id = db.Column(db.Integer)

    def __repr__(self):
        return '<Slide %r>' % self.name
