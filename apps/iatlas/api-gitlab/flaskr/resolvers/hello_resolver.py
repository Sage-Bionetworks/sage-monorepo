from ariadne import ObjectType
from flaskr.db_models import Class

hello = ObjectType("Query")


@hello.field("hello")
def resolve_hello(_obj, info):
    request = info.context
    user_agent = request.headers.get("User-Agent")
    return "Hello, %s!" % user_agent
