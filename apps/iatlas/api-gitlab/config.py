import os


def get_database_uri():
    HOST = os.environ['POSTGRES_HOST']
    if 'POSTGRES_PORT' in os.environ:
        HOST = HOST + ':' + os.environ['POSTGRES_PORT']
    POSTGRES = {
        'user': os.environ['POSTGRES_USER'],
        'pw': os.environ['POSTGRES_PASSWORD'],
        'db': os.environ['POSTGRES_DB'],
        'host': HOST,
    }
    DATABASE_URI = 'postgresql://%(user)s:%(pw)s@%(host)s/%(db)s' % POSTGRES
    if 'DATABASE_URI' in os.environ:
        DATABASE_URI = os.environ['DATABASE_URI']
    return DATABASE_URI


class Config(object):
    SQLALCHEMY_DATABASE_URI = get_database_uri()
    SQLALCHEMY_TRACK_MODIFICATIONS = False
