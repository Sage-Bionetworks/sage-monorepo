from flaskr import db
from . import Base
from flaskr.enums import direction_enum


class DriverResult(Base):
    __tablename__ = 'driver_results'
    id = db.Column(db.Integer, primary_key=True)
    p_value = db.Column(db.Float, nullable=True)
    fold_change = db.Column(db.Float, nullable=True)
    log10_p_value = db.Column(db.Float, nullable=True)
    log10_fold_change = db.Column(db.Float, nullable=True)
    n_wt = db.Column(db.Integer, nullable=True)
    n_mut = db.Column(db.Integer, nullable=True)

    feature_id = db.Column(db.Integer, db.ForeignKey(
        'feature.id'), nullable=False)

    gene_id = db.Column(db.Integer, db.ForeignKey('genes.id'), nullable=False)

    mutation_code_id = db.Column(db.Integer, db.ForeignKey(
        'mutation_code.id'), nullable=False)

    tag_id = db.Column(db.Integer, db.ForeignKey('tags.id'), nullable=False)

    def __repr__(self):
        return '<DriverResult %r>' % self.id
