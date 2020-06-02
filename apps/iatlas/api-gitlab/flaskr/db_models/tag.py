from flaskr import db


class Tag(db.Model):
    __tablename__ = 'tags'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)
    characteristics = db.Column(db.String, nullable=True)
    display = db.Column(db.String, nullable=True)
    color = db.Column(db.String, nullable=True)

    def __repr__(self):
        return '<Tag %r>' % self.name
