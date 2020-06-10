from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import GeneToSample


def return_gene_to_sample_query():
    return db.session.query(GeneToSample)
