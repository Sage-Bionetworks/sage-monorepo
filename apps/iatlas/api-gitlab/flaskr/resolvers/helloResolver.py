from ariadne import ObjectType

hello = ObjectType("Query")


@hello.field("hello")
def resolve_hello(_obj, info):
    request = info.context
    user_agent = request.headers.get("User-Agent", "Guest")
    return "Hello, %s!" % user_agent
