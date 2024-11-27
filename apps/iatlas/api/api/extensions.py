from flask_sqlalchemy import SQLAlchemy
from api.logger import LogSetup

db = SQLAlchemy()
logs = LogSetup()
