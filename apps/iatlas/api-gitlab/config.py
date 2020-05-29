import os


def get_database_uri():
    POSTGRES = {
        'user': os.environ['PG_USER'],
        'pw': os.environ['PG_PW'],
        'db': os.environ['PG_DATABASE'],
        'host': os.environ['PG_HOST'],
        'port': os.environ['PG_PORT']
    }
    DATABASE_URI = 'postgresql://%(user)s:%(pw)s@%(host)s:%(port)s/%(db)s' % POSTGRES
    if 'DATABASE_URI' in os.environ:
        DATABASE_URI = os.environ['DATABASE_URI']
    return DATABASE_URI


class Config(object):
    SQLALCHEMY_DATABASE_URI = get_database_uri()
    SQLALCHEMY_TRACK_MODIFICATIONS = False
