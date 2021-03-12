import logging
from json import loads
from datetime import datetime as dt
from flask import Flask, request
from config import get_config
from .extensions import db, logs


def create_app(test=False):
    config = get_config(test=test)
    app = Flask(__name__)
    app.config.from_object(config)

    register_extensions(app)

    # Blueprint registration here.
    from .main import bp as main_bp
    app.register_blueprint(main_bp)

    @app.after_request
    def after_request(response):
        """ Logging after every POST request only if it isn't an introspection query. """
        json_data = request.get_json()
        is_introspection_query = bool(json_data and json_data.get(
            'operationName', False) == 'IntrospectionQuery')
        if request.method == 'POST' and not is_introspection_query:
            logger = logging.getLogger('api.access')
            logger.info(
                '%s [%s] %s %s %s %s %s %s %s',
                request.remote_addr,
                dt.utcnow().strftime('%d/%b/%Y:%H:%M:%S.%f')[:-3],
                request.method,
                request.path,
                request.scheme,
                response.status,
                response.content_length,
                request.referrer,
                request.user_agent,
            )
        return response

    @ app.teardown_appcontext
    def shutdown_session(exception=None):
        db.session.remove()

    return app


def register_extensions(app):
    db.init_app(app)
    logs.init_app(app)
    return None


from api import db_models
