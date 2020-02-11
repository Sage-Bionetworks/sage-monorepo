.GlobalEnv$DB_NAME <- "iatlas_dev"
.GlobalEnv$DB_HOST <- "localhost"
.GlobalEnv$DB_PORT <- "5432"
.GlobalEnv$DB_USER <- "postgres"
.GlobalEnv$DB_PW   <- "docker"
.GlobalEnv$pool    <- connect_to_db()
