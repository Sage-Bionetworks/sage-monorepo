from flaskr import db


class Mutation(db.Model):
    __tablename__ = 'mutations'
    id = db.Column(db.Integer, primary_key=True)

    gene_id = db.Column(db.Integer, db.ForeignKey('gene.id'), nullable=False)

    mutation_code_id = db.Column(
        db.Integer, db.ForeignKey('mutation_code.id'), nullable=True)

    mutation_type_id = db.Column(
        db.Integer, db.ForeignKey('mutation_type.id'), nullable=True)

    def __repr__(self):
        return '<Mutation %r>' % self.id
