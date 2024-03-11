# iAtlas API Logging

[BACK TO MAIN README](./../../README.md)

Logging is a great way to capture application information.

Logs can be captured at various levels:

- DEBUG
- WARN
- INFO
- ERROR

The application initializes logging when it is created (see `create_app` in [`api/__init__.py`](api/__init__.py)). The formatting is determined by values in the config (see [`config.py`](./../../config.py)).

The `development` environment is set at log level DEBUG, the `staging` environment is set at log level INFO, and the `production` environment is set to log level WARN. Tests are set to log level INFO.

To use logging, import logging, get the logger you want to use, and create a log of the appropriate level:

```python
import logging

logger = logging.getLogger('logger name here')

logger.debug('This is a debugging log')
logger.warn('This is a warning log')
logger.info('This is an info log')
logger.error('This is an error log')
```

Logs are saved to the `.logs/` folder in the root of the project. This folder is NOT versioned in the repository. Older logs are moved to a timestamped log file and newer logs are saved to the main `iatlas-api.log` file.
