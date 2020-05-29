#!/usr/bin/env python

from flaskr import db
from config import Config
import os
import tempfile
import pytest


class TestConfig(Config):
    TESTING = True

# @pytest.fixture
# def client():
#     app.config['TESTING'] = True

#     with app.test_client() as client:
#         with app.app_context():
#             pass
#         yield client
