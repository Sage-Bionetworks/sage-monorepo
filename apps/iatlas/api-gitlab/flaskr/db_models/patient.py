from flaskr import db


class Patient(db.Model):
    __tablename__ = 'patients'
    id = db.Column(db.Integer, primary_key=True)
    age = db.Column(db.Integer)
    barcode = db.Column(db.String)
    ethnicity = db.Column(db.String)
    gender = db.Column(db.String)
    height = db.Column(db.Integer)
    race = db.Column(db.String)
    weight = db.Column(db.Integer)

    def __repr__(self):
        return '<Patient %r>' % self.barcode
