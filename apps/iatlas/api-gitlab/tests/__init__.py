from config import Config
from api import db
import logging


NoneType = type(None)


class TestConfig(Config):
    LOG_LEVEL = logging.INFO
    PROFILE = False
    SQLALCHEMY_ECHO = False
    TESTING = True
