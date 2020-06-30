with_test_db_env <- function(expr){
  load_config('test', quiet = TRUE)
  with_global_db_pool(expr)
  on.exit(load_config('dev', quiet = TRUE))
}

with_test_api_env <- function(expr){
    if (present(.GlobalEnv$ghql_query_object)) rm(.GlobalEnv$ghql_query_object)
    iatlas.app::create_and_add_all_queries_to_qry_obj(
        query_dir = "../../queries"
    )
    expr
}
