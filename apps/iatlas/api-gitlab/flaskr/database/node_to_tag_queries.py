from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import NodeToTag


def return_node_to_tag_query():
    return db.session.query(NodeToTag)
