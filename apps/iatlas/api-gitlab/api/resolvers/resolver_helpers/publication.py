simple_publication_request_fields = {
    'doId', 'firstAuthorLastName', 'journal', 'name', 'pubmedId', 'title', 'year'}

publication_request_fields = simple_publication_request_fields.union({
                                                                     'genes', 'geneTypes'})
