from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Edge


def return_edge_query():
    return db.session.query(Edge)
