from ariadne import ObjectType
from flaskr.db_models import Class

hello = ObjectType("Query")


@hello.field("hello")
def resolve_hello(_obj, info):
    request = info.context
    user_agent = request.headers.get("User-Agent", "Guest")
    print("Classes: ", Class)
    return "Hello, %s!" % user_agent
