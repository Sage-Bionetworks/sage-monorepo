from sqlalchemy import orm
from api import db
from . import Base
from api.enums import unit_enum


class Feature(Base):
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

    feature_class = db.relationship("FeatureClass", backref=orm.backref(
        'features', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    method_tag = db.relationship("MethodTag", backref=orm.backref(
        'features', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    samples = db.relationship(
        "Sample", secondary='features_to_samples', uselist=True, lazy='noload')

    def __repr__(self):
        return '<Feature %r>' % self.name
