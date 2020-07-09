from api.db_models import (Slide)
from .resolver_helpers import get_value, build_option_args
from api.database import return_slide_query


valid_slide_node_mapping = {
    'id': 'id',
    'name': 'name',
    'description': 'description',
    'patient': 'patient_id'
}


def resolve_slide(_obj, info, id=None, name=None):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        valid_slide_node_mapping
    )
    query = return_slide_query(*option_args)
    if id != None:
        slide = query.filter_by(id=id).first()
    elif name != None:
        slide = query.filter_by(name=name).first()
    else:
        return None

    return {
        "id": get_value(slide, 'id'),
        "name": get_value(slide, 'name'),
        "description": get_value(slide, 'description'),
        "patient": get_value(slide, 'patient')
    }


def resolve_slides(_obj, info, id=None, name=None):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        valid_slide_node_mapping
    )
    query = return_slide_query(*option_args)
    if id is not None:
        query = query.filter(Slide.id.in_(id))
    elif name is not None:
        query = query.filter(Slide.name.in_(name))
    else:
        return None
    slides = query.all()
    return [{
        "id": get_value(slide, 'id'),
        "name": get_value(slide, 'name'),
        "description": get_value(slide, 'description'),
        "patient": get_value(slide, 'patient')
    } for slide in slides]
