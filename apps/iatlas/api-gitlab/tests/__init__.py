from config import Config
from flaskr import db


NoneType = type(None)


class TestConfig(Config):
    TESTING = True
