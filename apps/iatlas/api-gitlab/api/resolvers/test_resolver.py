def resolve_test(_obj, info):
    request = info.context
    user_agent = request.headers.get("User-Agent")
    return "Hello, %s!" % user_agent
