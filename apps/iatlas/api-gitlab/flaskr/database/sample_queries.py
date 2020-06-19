from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Patient, Sample, Slide
from .database_helpers import build_general_query

sample_related_fields = ['features', 'genes']

sample_core_fields = [
    'id', 'name', 'patient_id']


def return_sample_query(*args):
    return build_general_query(
        Patient, args=args,
        accepted_option_args=sample_related_fields,
        accepted_query_args=sample_core_fields)
