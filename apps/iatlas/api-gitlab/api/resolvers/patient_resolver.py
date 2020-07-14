from api.db_models import (Patient)
from .resolver_helpers import get_value, build_option_args
from api.database import return_patient_query


valid_patient_node_mapping = {
    'age': 'age',
    'barcode': 'barcode',
    'ethnicity': 'ethnicity',
    'gender': 'gender',
    'height': 'height',
    'weight': 'weight',
    'race': 'race'
}


def resolve_patients(_obj, info, barcode):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        valid_patient_node_mapping
    )
    query = return_patient_query(*option_args)
    if barcode:
        query = query.filter(Patient.barcode.in_(barcode))
    patients = query.distinct().all()
    return [{
        'age': get_value(patient, 'age'),
        'barcode': get_value(patient, 'barcode'),
        'etnicity': get_value(patient, 'etnicity'),
        'gender': get_value(patient, 'gender'),
        'height': get_value(patient, 'height'),
        'weight': get_value(patient, 'weight'),
        'race': get_value(patient, 'race')
    } for patient in patients]
