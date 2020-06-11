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
    references = db.Column(db.ARRAY(db.String), nullable=True)

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

    gene_family = db.relationship('GeneFamily', uselist=False, lazy='noload')

    gene_function = db.relationship(
        'GeneFunction', uselist=False, lazy='noload')

    immune_checkpoint = db.relationship(
        'ImmuneCheckpoint', uselist=False, lazy='noload')

    node_type = db.relationship('NodeType', uselist=False, lazy='noload')

    pathway = db.relationship('Pathway', uselist=False, lazy='noload')

    super_category = db.relationship(
        'SuperCategory', uselist=False, lazy='noload')

    therapy_type = db.relationship('TherapyType', uselist=False, lazy='noload')

    gene_types = db.relationship(
        "GeneType", secondary='genes_to_types', lazy='noload')

    def __repr__(self):
        return '<Gene %r>' % self.entrez
