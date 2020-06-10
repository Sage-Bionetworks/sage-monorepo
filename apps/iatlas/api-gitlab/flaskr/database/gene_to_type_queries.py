from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import GeneToType


def return_gene_to_type_query():
    return db.session.query(GeneToType)
