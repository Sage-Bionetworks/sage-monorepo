from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Tag
from .database_helpers import build_general_query

related_fields = [
    'copy_number_results', 'driver_results', 'related_tags', 'samples', 'tags']

core_fields = ['id', 'name', 'characteristics', 'display', 'color']


def return_tag_query(*args):
    return build_general_query(
        Tag, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
