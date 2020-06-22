from sqlalchemy import orm
from flaskr import db
from . import Base


class Gene(Base):
    __tablename__ = 'genes'
    id = db.Column(db.Integer, primary_key=True)
    entrez = db.Column(db.Integer, nullable=False)
    hgnc = db.Column(db.String, nullable=False)
    description = db.Column(db.String, nullable=True)
    friendly_name = db.Column(db.String, nullable=True)
    io_landscape_name = db.Column(db.String, nullable=True)

    gene_family_id = db.Column(
        db.Integer, db.ForeignKey('gene_families.id'), nullable=True)

    gene_function_id = db.Column(
        db.Integer, db.ForeignKey('gene_functions.id'), nullable=True)

    immune_checkpoint_id = db.Column(
        db.Integer, db.ForeignKey('immune_checkpoints.id'), nullable=True)

    node_type_id = db.Column(
        db.Integer, db.ForeignKey('node_types.id'), nullable=True)

    pathway_id = db.Column(
        db.Integer, db.ForeignKey('pathways.id'), nullable=True)

    super_cat_id = db.Column(
        db.Integer, db.ForeignKey('super_categories.id'), nullable=True)

    therapy_type_id = db.Column(
        db.Integer, db.ForeignKey('therapy_types.id'), nullable=True)

    gene_family = db.relationship(
        'GeneFamily', backref=orm.backref('genes', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    gene_function = db.relationship(
        'GeneFunction', backref=orm.backref('genes', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    gene_types = db.relationship(
        "GeneType", secondary='genes_to_types', uselist=True, lazy='noload')

    immune_checkpoint = db.relationship(
        'ImmuneCheckpoint', backref=orm.backref('genes', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    node_type = db.relationship(
        'NodeType', backref=orm.backref('genes', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    pathway = db.relationship(
        'Pathway', backref=orm.backref('genes', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    publications = db.relationship(
        "Publication", secondary='publications_to_genes', uselist=True, lazy='noload')

    super_category = db.relationship(
        'SuperCategory', backref=orm.backref('genes', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    therapy_type = db.relationship(
        'TherapyType', backref=orm.backref('genes', uselist=True, lazy='noload'),
        uselist=False, lazy='noload')

    samples = db.relationship(
        "Sample", secondary='genes_to_samples', uselist=True, lazy='noload')

    def __repr__(self):
        return '<Gene %r>' % self.entrez
