from sqlalchemy import orm
from api import db
from . import Base


class SingleCellPseudobulkFeature(Base):
    __tablename__ = 'single_cell_pseudobulk_features'
    id = db.Column(db.String, primary_key=True)
    cell_type = db.Column(db.String, nullable=False)
    value = db.Column(db.Numeric, nullable=False)

    feature_id = db.Column(db.String, db.ForeignKey(
        'features.id'), primary_key=True)

    sample_id = db.Column(db.String, db.ForeignKey(
        'samples.id'), primary_key=True)

    feature = db.relationship('Feature', backref=orm.backref(
        'pseudobulk_feature_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    sample = db.relationship('Sample', backref=orm.backref(
        'pseudobulk_feature_assoc', uselist=True, lazy='noload'), uselist=False, lazy='noload')

    def __repr__(self):
        return '<SingleCellPseudobulkFeature %r>' % self.feature_id