NoneType = type(None)


def build_option_args(selection_set=None, valid_nodes={}):
    option_args = []
    if type(selection_set) is not NoneType:
        for selection in selection_set.selections:
            if selection.name.value in valid_nodes:
                option_args.append(valid_nodes.get(selection.name.value))
    return option_args


def get_child_value(parent=None, field="name"):
    if parent is not None:
        return get_value(parent, field)
    return None


def get_selection_set(selection_set, condition=True, child_node='features'):
    if condition and type(selection_set) is not NoneType:
        for selection in selection_set.selections:
            if selection.name.value == child_node:
                selection_set = selection.selection_set
                break
    return selection_set


def get_value(obj, attribute):
    if hasattr(obj, attribute):
        return getattr(obj, attribute)
    return None
