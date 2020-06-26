from sqlalchemy import orm
from api import db
from . import Base
from api.enums import direction_enum


class CopyNumberResult(Base):
    __tablename__ = 'copy_number_results'
    id = db.Column(db.Integer, primary_key=True)
    direction = db.Column(direction_enum, nullable=False)
    mean_normal = db.Column(db.Float, nullable=True)
    mean_cnv = db.Column(db.Float, nullable=True)
    p_value = db.Column(db.Float, nullable=True)
    log10_p_value = db.Column(db.Float, nullable=True)
    t_stat = db.Column(db.Float, nullable=True)

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'features.id'), nullable=False)

    gene_id = db.Column(db.Integer, db.ForeignKey('genes.id'), nullable=False)

    tag_id = db.Column(db.Integer, db.ForeignKey('tags.id'), nullable=False)

    feature = db.relationship('Feature', backref=orm.backref(
        'copy_number_results', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    gene = db.relationship('Gene', backref=orm.backref(
        'copy_number_results', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    tag = db.relationship('Tag', backref=orm.backref(
        'copy_number_results', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    def __repr__(self):
        return '<CopyNumberResult %r>' % self.id
