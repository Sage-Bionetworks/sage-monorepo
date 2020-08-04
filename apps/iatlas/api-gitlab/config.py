import os


def get_database_uri():
    HOST = os.environ['POSTGRES_HOST']
    if 'POSTGRES_PORT' in os.environ and os.environ['POSTGRES_PORT'] != 'None':
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


BASE_PATH = os.path.dirname(os.path.abspath(__file__))


class Config(object):
    LOG_PATH = os.path.join(BASE_PATH, '.logs')
    LOG_FILE = os.path.join(LOG_PATH, 'server.log')
    PROFILE = True
    PROFILE_PATH = os.path.join(BASE_PATH, '.profiles')
    SQLALCHEMY_DATABASE_URI = get_database_uri()
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_ENGINE_OPTIONS = {'pool_pre_ping': True}
