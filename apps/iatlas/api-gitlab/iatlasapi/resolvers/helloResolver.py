from ariadne import QueryType


hello = QueryType()


@hello.field("hello")
def resolve_hello(_obj, info):
    request = info.context
    user_agent = request.headers.get("User-Agent", "Guest")
    return "Hello, %s!" % user_agent