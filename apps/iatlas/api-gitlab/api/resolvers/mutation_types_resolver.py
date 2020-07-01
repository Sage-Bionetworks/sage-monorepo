from sqlalchemy import orm
from api import db
from api.database import return_mutation_type_query
from api.db_models import MutationType
from .resolver_helpers import build_option_args, get_selection_set, get_value


def resolve_mutation_types(_obj, info, dataSet=None, sample=None):
    sess = db.session

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)

    mutation_type_1 = orm.aliased(MutationType, name='mt')

    core_field_mapping = {'display': mutation_type_1.display.label('display'),
                          'name': mutation_type_1.name.label('name')}
    core = build_option_args(selection_set, core_field_mapping)

    mutation_types = sess.query(*core).distinct().all()

    return [{
        'display': get_value(mutation_type, 'display'),
        'name': get_value(mutation_type)
    } for mutation_type in mutation_types]
