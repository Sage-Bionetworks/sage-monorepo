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

    gene = db.relationship("Gene")
    mutation_code = db.relationship("MutationCode")
    mutation_type = db.relationship("MutationType")
    samples = db.relationship("Sample", secondary='samples_to_mutations')

    def __repr__(self):
        return '<Mutation %r>' % self.id
