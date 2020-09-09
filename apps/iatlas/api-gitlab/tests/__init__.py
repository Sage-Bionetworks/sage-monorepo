from config import Config
from api import db


NoneType = type(None)


class TestConfig(Config):
    LOG_LEVEL = 'INFO'
    PROFILE = False
    SQLALCHEMY_ECHO = False
    TESTING = True
