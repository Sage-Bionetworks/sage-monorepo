#' Create a database connection
#'
#' @param dbname String
#' @param host String
#' @param port String
#' @param user String
#' @param password String
#' @importFrom pool dbPool
#' @importFrom RPostgres Postgres
connect_to_db <- function(
  dbname   = .GlobalEnv$DB_NAME,
  host     = .GlobalEnv$DB_HOST,
  port     = .GlobalEnv$DB_PORT,
  user     = .GlobalEnv$DB_USER,
  password = .GlobalEnv$DB_PW
) {
  return(pool::dbPool(
    # Connect to a PostgreSQL database.
    drv = RPostgres::Postgres(),
    # The database name, host name, port, username,
    # and password all are set in environment variables by default.
    dbname = dbname,
    host = host,
    port = port,
    user = user,
    password = password,
    # The R type that 64-bit integer types should be mapped to,
    # default is bit64::integer64, which allows the full range of 64 bit
    # integers
    # See: http://www.win-vector.com/blog/2018/03/take-care-if-trying-the-rpostgres-package/
    bigint = "numeric",
    # Limit the max pool size to 10.
    maxSize = 10
  ))
}

present <- function (a) {!identical(a, NA) && !identical(a, NULL)}

create_global_db_pool <- function() {
  if (!present(.GlobalEnv$pool)) {
    .GlobalEnv$pool <- connect_to_db()
  } else {
    cat(crayon::yellow("WARNING-create_global_db_pool: global db pool already created\n"))
    .GlobalEnv$pool
  }
}

vivify_global_db_pool <- function() {
  if (!present(.GlobalEnv$pool)) create_global_db_pool()
  else .GlobalEnv$pool
}

release_global_db_pool <- function() {
  if (present(.GlobalEnv$pool)) {
    pool::poolClose(.GlobalEnv$pool)
    rm(pool, pos = ".GlobalEnv")
  } else {
    cat(crayon::yellow("WARNING-release_global_db_pool: Nothing to do. Global db pool does not exist. \n"))
  }
}
