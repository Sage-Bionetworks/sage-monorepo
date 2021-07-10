from .general_resolvers import get_value


def build_simple_tag_graphql_response(prefix='tag_'):

    def f(tag):
        if not tag:
            return None
        else:
            dict = {
                'id': get_value(tag, prefix + 'id'),
                'name': get_value(tag, prefix + 'name') or get_value(tag, 'name'),
                'characteristics': get_value(tag, prefix + 'characteristics'),
                'color': get_value(tag, prefix + 'color'),
                'longDisplay': get_value(tag, prefix + 'long_display'),
                'shortDisplay': get_value(tag, prefix + 'short_display')
            }
            return(dict)
    return(f)
