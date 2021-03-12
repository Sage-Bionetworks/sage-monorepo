def build_join_condition(join_column, column, filter_column=None, filter_list=None):
    '''
    A helper function that creates a list of conditions.

    All positional arguments are required. TheyPositional arguments are:
        1st position - a column from the joining table or value who's value is to be compared as equal to the value passed in 2nd position
        2nd position - a column or value who's value is to be compared as equal to the value passed in ist position

        ie: join_column == column

    All keyword arguments are optional. Keyword arguments are:
        `filter_column` - the column that will have the `in_` filter applied to it. This will only be applied if an actual value is passed to `filter_list`.
        `filter_list` - A list of values to be applied to the `in_` filter. This may be a list of values or a subquery that returns a list.
    '''
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
    '''
    A helper function to get the selection set from a graphql request.

    All keyword arguments are optional. Keyword arguments are:
        `selection_set` - an initial set to get the selection set from. Defaults to an empty list.
        `child_node` - the node one level down to look in. If this has no value, the root of the set it used. Defaults to None.
        `info` - the info object from a request. If this is passed, the selection set will be taken from here and any passed selection set will be ignored. Defaults to None
    '''
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
    '''
    A helper function to get attribute values from an object.

    All keyword arguments are optional. Keyword arguments are:
        `obj` - the object to get the value from. If no object is passed or the value is None, the default will be returned. Defaults to None.
        `attribute` - the attribute name to look for. Defaults to 'name'.
        `default` - the default value to return if the attribute is not found. Defaults to None
    '''
    if obj:
        return getattr(obj, attribute, default)
    return default
