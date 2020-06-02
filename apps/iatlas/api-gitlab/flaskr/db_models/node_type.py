from flaskr import db


class NodeType(db.Model):
    __tablename__ = 'node_types'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<NodeType %r>' % self.name
