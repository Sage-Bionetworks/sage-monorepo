from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Patient, Sample, Slide


def return_patient_query():
    return db.session.query(Patient)


def return_sample_query(*argv):
    accepted_args = ['mutations', 'tags']
    args = []
    for arg in argv:
        if arg in accepted_args:
            args.append(orm.joinedload(arg))
    return db.session.query(Sample).options(args)


def return_slide_query():
    return db.session.query(Slide)
