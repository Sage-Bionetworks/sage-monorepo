def build_option_args(selection_set=None, valid_nodes={}):
    option_args = []
    if selection_set is not None:
        for selection in selection_set.selections:
            if selection.name.value in valid_nodes:
                option_args.append(valid_nodes.get(selection.name.value))
    return option_args


def get_field_value(parent=None, field="name"):
    if parent is not None:
        return getattr(parent, field)
    return None
