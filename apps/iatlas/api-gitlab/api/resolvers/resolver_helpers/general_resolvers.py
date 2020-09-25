def build_join_condition(join_column, column, filter_column=None, filter_list=None):
    join_condition = [join_column == column]
    if bool(filter_list):
        join_condition.append(filter_column.in_(filter_list))
    return join_condition


def build_option_args(selection_set=None, valid_nodes={}):
    option_args = set()
    add_to_option_args = option_args.add
    if selection_set:
        for selection in selection_set.selections:
            if selection.name.value in valid_nodes:
                if isinstance(valid_nodes, set):
                    add_to_option_args(selection.name.value)
                else:
                    add_to_option_args(valid_nodes.get(selection.name.value))
    return option_args


def get_requested(info=None, requested_field_mapping={}, child_node=None, selection_set=[]):
    selection_set = get_selection_set(selection_set, child_node, info)
    return build_option_args(selection_set, requested_field_mapping)


def get_selected(requested, selected_field_mapping):
    selected_keys = set([*selected_field_mapping]).intersection(requested)
    return set(map(selected_field_mapping.get, selected_keys))


def get_selection_set(selection_set=[], child_node=None, info=None):
    selection_set = info.field_nodes[0].selection_set if info else selection_set
    if selection_set and child_node:
        new_selection_set = []
        for selection in selection_set.selections:
            if selection.name.value == child_node:
                new_selection_set = selection.selection_set
                break
        return new_selection_set

    return selection_set


def get_value(obj=None, attribute='name', default=None):
    if obj:
        return getattr(obj, attribute, default)
    return default
