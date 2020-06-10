from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Node


def return_node_query():
    return db.session.query(Node)
