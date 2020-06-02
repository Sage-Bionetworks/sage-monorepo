from flaskr import db


class Sample(db.Model):
    __tablename__ = 'samples'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String)
    patient_id = db.Column(db.Integer)

    def __repr__(self):
        return '<Sample %r>' % self.name
