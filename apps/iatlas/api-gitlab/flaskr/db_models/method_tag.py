from flaskr import db


class MethodTag(db.Model):
    __tablename__ = 'method_tags'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<MethodTag %r>' % self.name
