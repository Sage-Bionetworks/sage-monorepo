from sqlalchemy import orm
from api import db
from . import Base


class CellStat(Base):
    __tablename__ = 'cell_stats'
    id = db.Column(db.String, primary_key=True)
    cell_type = db.Column(db.String, nullable=False)
    cell_count = db.Column(db.Integer, nullable=True)
    avg_expr = db.Column(db.Numeric, nullable=True)
    perc_expr = db.Column(db.Numeric, nullable=True)


    dataset_id = db.Column(db.String, db.ForeignKey(
        'datasets.id'), nullable=False)

    gene_id = db.Column(db.String, db.ForeignKey(
        'genes.id'), nullable=False)

    data_set = db.relationship(
        'Dataset', backref=orm.backref('cell_stats', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    gene = db.relationship(
        'Gene', backref=orm.backref('cell_stats', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    def __repr__(self):
        return '<CellStat %r>' % self.id