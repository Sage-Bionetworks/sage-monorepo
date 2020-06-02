from flaskr import db


class Gene(db.Model):
    __tablename__ = 'genes'
    id = db.Column(db.Integer, primary_key=True)
    entrez = db.Column(db.Integer, nullable=False)
    hgnc = db.Column(db.String, nullable=False)

    gene_family_id = db.Column(
        db.Integer, db.ForeignKey('gene_family.id'), nullable=True)

    gene_function_id = db.Column(
        db.Integer, db.ForeignKey('gene_function.id'), nullable=True)

    immune_checkpoint_id = db.Column(
        db.Integer, db.ForeignKey('immune_checkpoint.id'), nullable=True)

    node_type_id = db.Column(
        db.Integer, db.ForeignKey('node_type.id'), nullable=True)

    pathway_id = db.Column(
        db.Integer, db.ForeignKey('pathway.id'), nullable=True)

    super_cat_id = db.Column(
        db.Integer, db.ForeignKey('super_category.id'), nullable='subquery')

    therapy_type_id = db.Column(
        db.Integer, db.ForeignKey('therapy_type.id'), nullable=True)

    description = db.Column(db.String, nullable=True)
    references = db.Column(db.ARRAY(db.String), nullable=True)
    io_landscape_name = db.Column(db.String, nullable=True)
    friendly_name = db.Column(db.String, nullable=True)

    def __repr__(self):
        return '<Gene %r>' % self.entrez
