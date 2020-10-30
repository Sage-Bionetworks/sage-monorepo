from logging.config import dictConfig
from os import makedirs, path

"""
We have options in python for stdout (streamhandling) and file logging
File logging has options for a Rotating file based on size or time (daily)
or a watched file, which supports logrotate style rotation
Most of the changes happen in the handlers, lets define a few standards
Borrowed HEAVILY from https://medium.com/tenable-techblog/the-boring-stuff-flask-logging-21c3a5dd0392
"""


class LogSetup(object):
    def __init__(self, app=None, **kwargs):
        if app is not None:
            self.init_app(app, **kwargs)

    def init_app(self, app):
        config = app.config
        log_type = config['LOG_TYPE']
        logging_level = config['LOG_LEVEL']
        log_extension = '.log'
        if log_type != 'stream':
            try:
                log_directory = config['LOG_DIR']
                app_log_file_name = config['LOG_APP_NAME'] + log_extension
                access_log_file_name = config['LOG_WWW_NAME'] + log_extension
                if not path.exists(log_directory):
                    makedirs(log_directory)
            except KeyError as e:
                exit(code="{} is a required parameter for log_type '{}'".format(
                    e, log_type))
            path_sep = path.sep
            app_log = path_sep.join([log_directory, app_log_file_name])
            www_log = path_sep.join([log_directory, access_log_file_name])

        if log_type == 'stream':
            logging_policy = 'logging.StreamHandler'
        elif log_type == 'watched':
            logging_policy = 'logging.handlers.WatchedFileHandler'
        else:
            log_copies = config['LOG_COPIES']
            logging_policy = 'logging.handlers.TimedRotatingFileHandler'
            log_time_interval = config['LOG_TIME_INT']
            log_interval = config['LOG_INTERVAL']

        std_format = {
            'formatters': {
                'default': {
                    'format': '[%(asctime)s.%(msecs)03d] %(levelname)s %(name)s:%(funcName)s: %(message)s',
                    'datefmt': '%d/%b/%Y:%H:%M:%S',
                },
                'access': {'format': '%(message)s'},
            }
        }
        std_logger = {
            'loggers': {
                '': {'level': logging_level, 'handlers': ['default'], 'propagate': True},
                'app.access': {
                    'level': logging_level,
                    'handlers': ['access_logs'],
                    'propagate': False,
                },
                'root': {'level': logging_level, 'handlers': ['default']},
            }
        }
        if log_type == 'stream':
            logging_handler = {
                'handlers': {
                    'default': {
                        'level': logging_level,
                        'formatter': 'default',
                        'class': logging_policy,
                    },
                    'access_logs': {
                        'level': logging_level,
                        'class': logging_policy,
                        'formatter': 'access',
                    },
                }
            }
        elif log_type == 'watched':
            logging_handler = {
                'handlers': {
                    'default': {
                        'level': logging_level,
                        'class': logging_policy,
                        'filename': app_log,
                        'formatter': 'default',
                        'delay': True,
                    },
                    'access_logs': {
                        'level': logging_level,
                        'class': logging_policy,
                        'filename': www_log,
                        'formatter': 'access',
                        'delay': True,
                    },
                }
            }
        else:
            logging_handler = {
                'handlers': {
                    'default': {
                        'level': logging_level,
                        'class': logging_policy,
                        'filename': app_log,
                        'backupCount': log_copies,
                        'interval': log_interval,
                        'formatter': 'default',
                        'delay': True,
                        'when': log_time_interval
                    },
                    'access_logs': {
                        'level': logging_level,
                        'class': logging_policy,
                        'filename': www_log,
                        'backupCount': log_copies,
                        'interval': log_interval,
                        'formatter': 'access',
                        'delay': True,
                        'when': log_time_interval
                    },
                }
            }

        log_config = {
            'version': 1,
            'formatters': std_format['formatters'],
            'loggers': std_logger['loggers'],
            'handlers': logging_handler['handlers'],
        }
        dictConfig(log_config)
