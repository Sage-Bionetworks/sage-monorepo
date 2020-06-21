def build_option_args(selection_set=None, valid_nodes={}):
    option_args = []
    if selection_set:
        for selection in selection_set.selections:
            if selection.name.value in valid_nodes:
                option_args.append(valid_nodes.get(selection.name.value))
    return option_args


def get_selection_set(selection_set, condition=True, child_node='features'):
    if condition and selection_set:
        for selection in selection_set.selections:
            if selection.name.value == child_node:
                selection_set = selection.selection_set
                break
    return selection_set


def get_value(obj=None, attribute='name', default=None):
    if obj:
        return getattr(obj, attribute, default)
    return None
