with_test_api_env <- function(expr){
  query_dir  <- system.file("queries", package = "iatlas.app")

  create_and_add_all_queries_to_qry_obj(
    query_dir = query_dir
  )
  expr
}
