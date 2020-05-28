from flask import Flask
from flask_sqlalchemy import SQLAlchemy
import os

POSTGRES = {
    'user': os.environ['PG_USER'],
    'pw': os.environ['PG_PW'],
    'db': os.environ['PG_DATABASE'],
    'host': os.environ['PG_HOST'],
    'port': os.environ['PG_PORT']
}
DATABASE_URI = 'postgresql://%(user)s:%(pw)s@%(host)s:%(port)s/%(db)s' % POSTGRES
if 'DATABASE_URI' in os.environ:
    DATABASE_URI = os.environ['DATABASE_URI']

app = Flask(__name__)
app.debug = True
app.config['SQLALCHEMY_DATABASE_URI'] = DATABASE_URI

app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

from iatlasapi import db_models
from iatlasapi import routes
