with_test_db_env <- function(expr) {
  load_config('test', quiet=TRUE)
  with_global_db_pool(expr)
  on.exit(load_config('dev', quiet=TRUE))
}