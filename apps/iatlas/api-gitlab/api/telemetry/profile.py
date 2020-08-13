import cProfile
import datetime
import os
import time
import logging

from flask import current_app as app

log = logging.getLogger('profiling')
log.setLevel(logging.DEBUG)

# usage: @profile("profile_for_func1_001")


def profile(name):
    def inner(func):
        def wrapper(*args, **kwargs):
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


# Not currently compatible with Alpine
"""
import sys
from os.path import expanduser
from line_profiler import LineProfiler


def line_profile(f):
    lp = LineProfiler()
    lp_wrapper = lp(f)

    def wrapper(*args, **kwargs):
        val = lp_wrapper(*args, **kwargs)
        path = app.config['PROFILE_PATH']
        if not os.path.exists(path):
            os.makedirs(path)
        fname = '{}.txt'.format(f.__qualname__)
        file = open(os.path.join(path, fname, ), 'w', encoding='utf-8')
        lp.print_stats(stream=file, output_unit=1e-03)
        lp.print_stats(stream=sys.stdout, output_unit=1e-03)
        return val
    return wrapper
"""

from sqlalchemy import event
from sqlalchemy.engine import Engine


@event.listens_for(Engine, "before_cursor_execute")
def before_cursor_execute(conn, cursor, statement,
                          parameters, context, executemany):
    conn.info.setdefault('query_start_time', []).append(time.time())
    log.debug("Start Query: %s", statement)


@event.listens_for(Engine, "after_cursor_execute")
def after_cursor_execute(conn, cursor, statement,
                         parameters, context, executemany):
    total = time.time() - conn.info['query_start_time'].pop(-1)
    log.debug("Query Complete!")
    log.debug("Total Time: %f", total)


import cProfile
import io
import pstats
import contextlib


@contextlib.contextmanager
def profiled():
    pr = cProfile.Profile()
    pr.enable()
    yield
    pr.disable()
    s = io.StringIO()
    ps = pstats.Stats(pr, stream=s).sort_stats('cumulative')
    ps.print_stats()
    # uncomment this to see who's calling what
    # ps.print_callers()
    print(s.getvalue())
