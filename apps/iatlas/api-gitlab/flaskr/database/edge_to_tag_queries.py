from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import EdgeToTag


def return_edge_to_tag_query():
    return db.session.query(EdgeToTag)
