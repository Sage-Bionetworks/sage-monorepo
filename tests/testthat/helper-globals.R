with_test_db_env <- function(expr) {
  load_config('test')
  iatlas.app::vivify_global_db_pool()
  cat(crayon::yellow("with_test_db_env",.GlobalEnv$DB_NAME,"\n"))
  on.exit({
    load_config('dev')
    iatlas.app::release_global_db_pool()
  })
  foo <- expr
}