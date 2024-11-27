from api.db_models import Sample
from .database_helpers import build_general_query

sample_related_fields = ["features", "genes"]

sample_core_fields = ["id", "name", "patient_id"]


def return_sample_query(*args):
    return build_general_query(
        Sample,
        args=args,
        accepted_option_args=sample_related_fields,
        accepted_query_args=sample_core_fields,
    )
