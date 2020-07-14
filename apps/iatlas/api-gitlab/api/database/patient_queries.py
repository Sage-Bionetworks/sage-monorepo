from sqlalchemy import orm
from api import db
from api.db_models import Patient, Sample, Slide
from .database_helpers import build_general_query

patient_related_fields = ['samples', 'slides']

patient_core_fields = [
    'id', 'age', 'barcode', 'ethnicity', 'gender', 'height', 'race', 'weight']

sample_related_fields = ['data_sets',
                         'dataset_sample_assoc',
                         'feature_sample_assoc',
                         'features',
                         'gene_sample_assoc',
                         'genes',
                         'mutations',
                         'patient',
                         'sample_mutation_assoc',
                         'sample_tag_assoc',
                         'tags']

sample_core_fields = ['id', 'name', 'patient_id']

slide_related_fields = ['patient']

slide_core_fields = ['id', 'name', 'description']


def return_patient_query(*args):
    return build_general_query(
        Patient, args=args,
        accepted_option_args=patient_related_fields,
        accepted_query_args=patient_core_fields)


def return_sample_query(*args):
    return build_general_query(
        Sample, args=args,
        accepted_option_args=sample_related_fields,
        accepted_query_args=sample_core_fields)


def return_slide_query(*args):
    return build_general_query(
        Slide, args=args,
        accepted_option_args=slide_related_fields,
        accepted_query_args=slide_core_fields)
