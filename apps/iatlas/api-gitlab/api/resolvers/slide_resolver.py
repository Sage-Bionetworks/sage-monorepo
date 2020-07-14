from api.db_models import (Slide)
from .resolver_helpers import get_value, build_option_args
from api.database import return_slide_query


valid_slide_node_mapping = {
    'name': 'name',
    'description': 'description',
    'patient': 'patient'
}


def resolve_slides(_obj, info, name=None):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        valid_slide_node_mapping
    )
    query = return_slide_query(*option_args)
    if name:
        query = query.filter(Slide.name.in_(name))
    slides = query.distinct().all()
    return [{
        'name': get_value(slide, 'name'),
        'description': get_value(slide, 'description'),
        'patient': get_value(slide, 'patient')
    } for slide in slides]
