from flaskr import db
from flaskr.enums import unit_enum


class Feature(db.Model):
    __tablename__ = 'features'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)
    display = db.Column(db.String, nullable=True)
    order = db.Column(db.Integer, nullable=True)
    unit = db.Column(unit_enum, nullable=True)

    class_id = db.Column(db.Integer, db.ForeignKey(
        'classes.id'), nullable=False)

    method_tag_id = db.Column(
        db.Integer, db.ForeignKey('method_tags.id'), nullable=True)

    def __repr__(self):
        return '<Feature %r>' % self.name
