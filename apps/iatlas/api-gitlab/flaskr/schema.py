from ariadne import QueryType, load_schema_from_path, make_executable_schema

type_defs = load_schema_from_path("/project/flaskr/schema/")

query = QueryType()


@query.field("hello")
def resolve_hello(_, info):
    request = info.context
    user_agent = request.headers.get("User-Agent", "Guest")
    return "Hello, %s!" % user_agent


schema = make_executable_schema(type_defs, query)
