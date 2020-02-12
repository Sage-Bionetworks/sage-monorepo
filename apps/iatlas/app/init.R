load_config <- function(configName) {
  dbConfig = config::get("database", config = configName)
  .GlobalEnv$DB_NAME <- Sys.getenv("DB_NAME",  unset = dbConfig$name)
  .GlobalEnv$DB_HOST <- Sys.getenv("DB_HOST",  unset = dbConfig$host)
  .GlobalEnv$DB_PORT <- Sys.getenv("DB_PORT",  unset = dbConfig$port)
  .GlobalEnv$DB_USER <- Sys.getenv("DB_USER",  unset = dbConfig$user)
  .GlobalEnv$DB_PW   <- Sys.getenv("DB_PW",    unset = dbConfig$password)
  cat(crayon::blue("Database config loaded:", configName, "\n"))
}

load_config(Sys.getenv("R_CONFIG_ACTIVE", unset = "dev"))

cat(crayon::blue("SUCCESS: iatlas.app is ready to go.\n"))
cat(crayon::blue(paste0("RUN: ",crayon::bold("shiny::runApp()\n"))))