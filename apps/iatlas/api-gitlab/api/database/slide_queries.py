from flaskr.db_models import  Slide
from .database_helpers import general_core_fields, build_general_query

slide_related_fields = [
    'patient']

slide_core_fields = [
    'id', 'name', 'description', 'patient_id']


def return_slide_query(*args):
    return build_general_query(
        Slide, args=args,
        accepted_option_args=slide_related_fields,
        accepted_query_args=slide_core_fields)
