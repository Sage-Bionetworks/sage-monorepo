from config import Config
from api import db


NoneType = type(None)


class TestConfig(Config):
    TESTING = True
