def resolve_test(_obj, info):
    request = info.context
    headers = request.headers
    content_length = headers.get('Content-Length')
    content_type = headers.get('Content-Type')
    host = headers.get('Host')
    referer = headers.get('Referer')
    user_agent = headers.get('User-Agent')
    return {
        'items': {
            'contentType': content_type,
            'userAgent': user_agent,
            'headers': {
                'contentLength': content_length,
                'contentType': content_type,
                'host': host,
                'userAgent': user_agent
            }
        },
        'page': 1
    }
