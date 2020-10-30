import logging
from os import getenv
from api import create_app

environment = getenv('FLASK_ENV') or 'development'
print(f'Starting server with {environment} config')
app = create_app()

if __name__ == '__main__':
    logger = logging.getLogger(__name__)
    HOST = '0.0.0.0'
    PORT = getenv('FLASK_RUN_PORT') or '5000'
    SSL_ENABLED = False
    SSL_CONTEXT = 'adhoc'

    if SSL_ENABLED:
        try:
            app.run(HOST, PORT, threaded=True, ssl_context=SSL_CONTEXT)
        except Exception as e:
            logger.error(f'Error: {e}')
            logger.info(f'SSL Context: {SSL_CONTEXT}')
    else:
        app.run(HOST, PORT, threaded=True)
