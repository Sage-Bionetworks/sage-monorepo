from .general_resolvers import get_value

simple_publication_request_fields = {
    'doId', 'firstAuthorLastName', 'journal', 'name', 'pubmedId', 'title', 'year'}

publication_request_fields = simple_publication_request_fields.union({
                                                                     'genes', 'geneTypes'})


def build_publication_graphql_response(pub):
    if not pub:
        return None
    return {
        'firstAuthorLastName': get_value(pub, 'first_author_last_name'),
        'doId': get_value(pub, 'do_id'),
        'journal': get_value(pub, 'journal'),
        'name': get_value(pub, 'publication_name') or get_value(pub),
        'pubmedId': get_value(pub, 'pubmed_id'),
        'title': get_value(pub, 'title'),
        'year': get_value(pub, 'year')
    }
