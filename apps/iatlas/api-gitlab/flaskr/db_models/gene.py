from flaskr import db


class Gene(db.Model):
    __tablename__ = 'genes'
    id = db.Column(db.Integer, primary_key=True)
    entrez = db.Column(db.Integer, nullable=False)
    hgnc = db.Column(db.String, nullable=False)

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

    description = db.Column(db.String, nullable=True)
    references = db.Column(db.ARRAY(db.String), nullable=True)
    io_landscape_name = db.Column(db.String, nullable=True)
    friendly_name = db.Column(db.String, nullable=True)

    gene_family = db.relationship('GeneFamily', uselist=False, lazy='subquery')

    gene_function = db.relationship(
        'GeneFunction', uselist=False, lazy='subquery')

    immune_checkpoint = db.relationship(
        'ImmuneCheckpoint', uselist=False, lazy='subquery')

    node_type = db.relationship('NodeType', uselist=False, lazy='subquery')

    pathway = db.relationship('Pathway', uselist=False, lazy='subquery')

    super_category = db.relationship(
        'SuperCategory', uselist=False, lazy='subquery')

    therapy_type = db.relationship(
        'TherapyType', uselist=False, lazy='subquery')

    def __repr__(self):
        return '<Gene %r>' % self.entrez
