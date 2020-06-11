from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Patient, Sample, Slide
from .database_helpers import build_option_args


def return_patient_query():
    return db.session.query(Patient)


def return_sample_query(*argv):
    args = build_option_args(argv, accepted_args=['mutations', 'tags'])
    return db.session.query(Sample).options(*args)


def return_slide_query():
    return db.session.query(Slide)
