from ariadne import load_schema_from_path, make_executable_schema, ObjectType
import os
import flaskr.resolvers as resolvers

dirname, _filename = os.path.split(os.path.abspath(__file__))


get_data_set_query = load_schema_from_path(
    dirname + "/getDataSet.query.graphql")
root_query = load_schema_from_path(dirname + "/root.query.graphql")

type_defs = [root_query, get_data_set_query]


root = ObjectType("Query")
get_data_set = ObjectType("GetDataSet")

root.set_field('test', resolvers.resolve_test)
root.set_field('getDataSet', resolvers.resolve_getDataSet)


schema = make_executable_schema(type_defs, [root, get_data_set])
