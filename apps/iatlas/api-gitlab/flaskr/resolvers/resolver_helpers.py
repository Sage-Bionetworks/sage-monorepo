def build_option_args(selection_set=None, valid_nodes={}):
    option_args = []
    if selection_set is not None:
        for selection in selection_set.selections:
            if selection.name.value in valid_nodes:
                option_args.append(valid_nodes.get(selection.name.value))
    return option_args


def get_name(parent=None):
    if parent is not None:
        return parent.name
    return None
