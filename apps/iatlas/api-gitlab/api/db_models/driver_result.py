from sqlalchemy import orm
from api import db
from . import Base


class DriverResult(Base):
    __tablename__ = 'driver_results'
    id = db.Column(db.Integer, primary_key=True)
    p_value = db.Column(db.Numeric, nullable=True)
    fold_change = db.Column(db.Numeric, nullable=True)
    log10_p_value = db.Column(db.Numeric, nullable=True)
    log10_fold_change = db.Column(db.Numeric, nullable=True)
    n_wt = db.Column(db.Integer, nullable=True)
    n_mut = db.Column(db.Integer, nullable=True)

    dataset_id = db.Column(db.Integer, db.ForeignKey(
        'datasets.id'), nullable=False)

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'features.id'), nullable=False)

    mutation_id = db.Column(db.Integer, db.ForeignKey(
        'mutations.id'), nullable=False)

    tag_id = db.Column(db.Integer, db.ForeignKey('tags.id'), nullable=False)

    data_set = db.relationship(
        'Dataset', backref=orm.backref('driver_results', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    feature = db.relationship(
        'Feature', backref=orm.backref('driver_results', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    mutation = db.relationship(
        'Mutation', backref=orm.backref('driver_results', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    tag = db.relationship(
        'Tag', backref=orm.backref('driver_results', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    def __repr__(self):
        return '<DriverResult %r>' % self.id
