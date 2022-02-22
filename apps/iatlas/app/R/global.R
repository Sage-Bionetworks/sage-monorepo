.GlobalEnv$MODULES_TBL <- "inst/tsv/module_config.tsv" %>%
  readr::read_tsv(.) %>%
  dplyr::mutate(
    "label" = dplyr::if_else(.data$label == "none", .data$name, .data$label),
    "link" = stringr::str_c("link_to_", .data$name),
    "image" = stringr::str_c("images/", .data$name, ".png"),
    "server_function_string" = stringr::str_c(.data$name, "_server"),
    "ui_function_string" = stringr::str_c(.data$name, "_ui")
  )

if(file.exists("app_config.yml")){
  config_list <- yaml::read_yaml("app_config.yml")
} else {
  config_list <- yaml::read_yaml("default_app_config.yml")
}

.GlobalEnv$APP <- config_list$app

if(.GlobalEnv$APP == "live"){
  .GlobalEnv$API_URL <- "https://api.cri-iatlas.org/api"
} else if (.GlobalEnv$APP == "staging") {
  .GlobalEnv$API_URL <- "https://api-staging.cri-iatlas.org/api"
} else {
  stop("App must be either 'live' or 'staging'")
}
