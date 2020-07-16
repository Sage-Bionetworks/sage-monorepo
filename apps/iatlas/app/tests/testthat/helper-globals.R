with_test_api_env <- function(expr){
  if (iatlas.app::present(.GlobalEnv$ghql_query_object)){
    rm(ghql_query_object, envir = .GlobalEnv)
  }
  iatlas.app::create_and_add_all_queries_to_qry_obj(
    query_dir = "../../queries"
  )
  expr
}
