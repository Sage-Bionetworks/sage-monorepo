from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Patient, Sample, Slide
from .database_helpers import build_general_query

sample_related_fields = ['feature_sample_assoc',
                         'features',
                         'gene_sample_assoc',
                         'genes',
                         'mutations',
                         'sample_mutation_assoc',
                         'tags']

sample_core_fields = ['id', 'name', 'patient_id']


def return_patient_query():
    return db.session.query(Patient)


def return_sample_query(*args):
    return build_general_query(
        Sample, args=args,
        accepted_option_args=sample_related_fields,
        accepted_query_args=sample_core_fields)


def return_slide_query():
    return db.session.query(Slide)
