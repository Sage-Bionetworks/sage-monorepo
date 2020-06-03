from flaskr import db


class GeneToSample(db.Model):
    __tablename__ = 'genes_to_samples'

    gene_id = db.Column(db.Integer, db.ForeignKey('gene.id'), primary_key=True)

    sample_id = db.Column(db.Integer, db.ForeignKey(
        'sample.id'), nullable=False)

    rna_seq_expr = db.Column(db.Float, nullable=True)

    def __repr__(self):
        return '<GeneToSample %r>' % self.gene_id
