from sqlalchemy import orm
from sqlalchemy.ext.compiler import compiles
from sqlalchemy.sql.expression import ClauseElement, Executable
from sqlalchemy.sql import Select
from sqlalchemy.dialects import postgresql

from api import db

general_core_fields = ['id', 'name']


def build_general_query(model, args=[], accepted_option_args=[], accepted_query_args=[]):
    option_args = build_option_args(*args, accepted_args=accepted_option_args)
    query_args = build_query_args(
        model, *args, accepted_args=accepted_query_args)
    query = db.session.query(*query_args)
    if option_args:
        # If option args are found, the whole model must be queried.
        return db.session.query(model).options(*option_args)
    return db.session.query(*query_args)


def build_option_args(*args, accepted_args=[]):
    option_args = []
    for arg in args:
        if arg in accepted_args:
            option_args.append(orm.joinedload(arg))
    return option_args


def build_query_args(model, *argv, accepted_args=[]):
    query_args = []
    for arg in argv:
        if arg in accepted_args:
            query_args.append(getattr(model, arg))
    if not query_args:
        return [model]
    return query_args

def temp_table(name, query):
    e = db.session.get_bind()
    c = e.connect()
    trans = c.begin()
    c.execute(CreateTableAs(name, query))
    trans.commit()
    return c

class CreateTableAs(Select):
    def __init__(self, name, query, *arg, **kw):
        super(CreateTableAs, self).__init__(None, *arg, **kw)
        self.name = name
        self.query = query

@compiles(CreateTableAs)
def _create_table_as(element, compiler, **kw):
    text = element.query.statement.compile(dialect=postgresql.dialect(), compile_kwargs={'literal_binds': True})
    query = "CREATE TEMP TABLE %s AS %s" % (
        element.name,
        text
    )
    return query

def execute_sql(query, conn=None):
    if conn:
        return conn.execute(query)
    engine = db.session.get_bind()
    with engine.connect() as conn:
        return conn.execute(query)
