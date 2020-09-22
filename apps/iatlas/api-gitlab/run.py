import datetime
import logging
import os
from logging.handlers import TimedRotatingFileHandler
from urllib.parse import urlparse

from flask import make_response, redirect, request, session

from api import create_app

log = logging.getLogger(__name__)

config_name = os.getenv('FLASK_ENV') or 'production'
print('Starting server with config: {}'.format(config_name))
app = create_app()

if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO,
                        format='%(asctime)s [%(levelname)s]: %(message)s')
    formatter = logging.Formatter('%(asctime)s [%(levelname)s]: %(message)s')
    handler = TimedRotatingFileHandler(app.config['LOG_FILE'],
                                       when="D",
                                       interval=1,
                                       backupCount=10,
                                       utc=True)
    handler.setFormatter(formatter)
    handler.setLevel(logging.INFO)

    log = logging.getLogger(__name__)
    log.addHandler(handler)

    HOST = '0.0.0.0'#app.config['SERVER_HOST']
    PORT = '5000'#app.config['SERVER_PORT']

    DEBUG = FLASK_DEBUG = True#app.config['FLASK_DEBUG']

    # print(app.config)
    # for key, val in app.config.items():
    #     print(key, val)
    # print('debug:', DEBUG)
    SSL_ENABLED = False
    SSL_CONTEXT = 'adhoc'

    if SSL_ENABLED:
        try:
            app.run(HOST, PORT, threaded=True,
                    debug=DEBUG, ssl_context=SSL_CONTEXT)
        except Exception as e:
            log.error('Error: {}'.format(e))
            log.info('SSL Context: {}'.format(SSL_CONTEXT))
    else:
        app.run(HOST, PORT, threaded=True, debug=DEBUG)
