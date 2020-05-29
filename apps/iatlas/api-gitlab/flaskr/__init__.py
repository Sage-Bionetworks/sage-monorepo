from flask import Flask
from flask_sqlalchemy import SQLAlchemy
import os
from config import Config

db = SQLAlchemy()


def create_app(config_class=Config):
    app = Flask(__name__)
    app.config.from_object(config_class)

    db.init_app(app)

    # Blueprint registration here.
    from .main import bp as main_bp
    app.register_blueprint(main_bp)

    from .resolvers import bp as resolvers_bp
    app.register_blueprint(resolvers_bp)

    from .schema import bp as schema_bp
    app.register_blueprint(schema_bp)

    # Production specific logic here.
    if not app.debug and not app.testing:
        pass

    return app
