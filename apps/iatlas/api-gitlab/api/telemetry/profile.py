import cProfile
import datetime
import os
from flask import current_app as app

# usage: @profile("profile_for_func1_001")


def profile(name):
    def inner(func):
        def wrapper(*args, **kwargs):
            # if not app.config['PROFILE']:
            if not app.config['PROFILE']:
                return func(*args, **kwargs)
            prof = cProfile.Profile()
            retval = prof.runcall(func, *args, **kwargs)
            path = app.config['PROFILE_PATH']
            if not os.path.exists(path):
                os.makedirs(path)
            fname = func.__qualname__
            now = datetime.datetime.utcnow().timestamp()
            prof.dump_stats(os.path.join(
                path, '{}.{}-{}.profile'.format(name, fname, now)))
            return retval
        wrapper.__doc__ = func.__doc__
        wrapper.__name__ = func.__name__
        return wrapper
    return inner
