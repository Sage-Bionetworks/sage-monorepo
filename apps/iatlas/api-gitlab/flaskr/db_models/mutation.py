from flaskr import db
from . import Base


class Mutation(Base):
    __tablename__ = 'mutations'
    id = db.Column(db.Integer, primary_key=True)

    gene_id = db.Column(db.Integer, db.ForeignKey('genes.id'), nullable=False)

    mutation_code_id = db.Column(
        db.Integer, db.ForeignKey('mutation_codes.id'), nullable=True)

    mutation_type_id = db.Column(
        db.Integer, db.ForeignKey('mutation_types.id'), nullable=True)

    gene = db.relationship("Gene", lazy='noload', uselist=False)
    mutation_code = db.relationship("MutationCode", lazy='noload', uselist=False)
    mutation_type = db.relationship("MutationType", lazy='noload', uselist=False)
    samples = db.relationship("Sample", secondary='samples_to_mutations', lazy='noload', uselist=True)
    # gene = db.relationship('Gene', uselist=False)
    
    # mutation_code = db.relationship('MutationCode', uselist = False)
    
    # mutation_type = db.relationship('MutationType', uselist = False)

    def __repr__(self):
        return '<Mutation %r>' % self.id
