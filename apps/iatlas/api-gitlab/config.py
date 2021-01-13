from os import environ, path
import logging


def get_database_uri():
    HOST = environ['POSTGRES_HOST']
    if 'POSTGRES_PORT' in environ and environ['POSTGRES_PORT'] != 'None':
        HOST = HOST + ':' + environ['POSTGRES_PORT']
    POSTGRES = {
        'user': environ['POSTGRES_USER'],
        'pw': environ['POSTGRES_PASSWORD'],
        'db': environ['POSTGRES_DB'],
        'host': HOST,
    }
    DATABASE_URI = 'postgresql://%(user)s:%(pw)s@%(host)s/%(db)s' % POSTGRES
    if 'DATABASE_URI' in environ:
        DATABASE_URI = environ['DATABASE_URI']
    return DATABASE_URI


BASE_PATH = path.dirname(path.abspath(__file__))


class Config(object):
    LOG_APP_NAME = 'iatlas-api'
    LOG_COPIES = 10
    LOG_DIR = path.join(BASE_PATH, '.logs')
    LOG_FILE = path.join(LOG_DIR, 'server.log')
    LOG_INTERVAL = 1
    LOG_LEVEL = logging.DEBUG
    LOG_TIME_INT = 'D'
    LOG_TYPE = 'TimedRotatingFile'
    LOG_WWW_NAME = 'iatlas-api-access'
    PROFILE = True
    PROFILE_PATH = path.join(BASE_PATH, '.profiles')
    SQLALCHEMY_DATABASE_URI = get_database_uri()
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_ENGINE_OPTIONS = {'pool_pre_ping': True}
    SQLALCHEMY_ECHO = True


class TestConfig(Config):
    LOG_LEVEL = logging.INFO
    PROFILE = False
    SQLALCHEMY_ECHO = False
    SQLALCHEMY_DATABASE_URI = get_database_uri()
    TESTING = True


class StagingConfig(Config):
    LOG_LEVEL = logging.INFO
    LOG_TYPE = 'stream'
    PROFILE = False
    SQLALCHEMY_ECHO = False


class ProdConfig(Config):
    LOG_LEVEL = logging.WARN
    LOG_TYPE = 'stream'
    PROFILE = False
    SQLALCHEMY_ECHO = False


def get_config(test=False):
    if (test):
        return TestConfig
    FLASK_ENV = environ['FLASK_ENV']
    if FLASK_ENV == 'development':
        return Config
    elif FLASK_ENV == 'staging':
        return StagingConfig
    else:
        return ProdConfig
