from .general_resolvers import get_value


def build_simple_tag_graphql_response(tag):
    result = {
        'id': get_value(tag, 'tag_id') or get_value(tag, 'id'),
        'name': get_value(tag, 'tag_name') or get_value(tag, 'name'),
        'characteristics': get_value(tag, 'tag_characteristics') or get_value(tag, 'characteristics'),
        'color': get_value(tag, 'tag_color') or get_value(tag, 'color'),
        'longDisplay': get_value(tag, 'tag_long_display') or get_value(tag, 'long_display'),
        'shortDisplay': get_value(tag, 'tag_short_display') or get_value(tag, 'short_display')
    }
    return(result)
