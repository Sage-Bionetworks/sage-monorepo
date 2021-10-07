MODULES_TBL <- "inst/tsv/module_config.tsv" %>%
  readr::read_tsv(.) %>%
  dplyr::mutate(
    "label" = dplyr::if_else(.data$label == "none", .data$name, .data$label),
    "link" = stringr::str_c("link_to_", .data$name),
    "image" = stringr::str_c("images/", .data$name, ".png"),
    "server_function_string" = stringr::str_c(.data$name, "_server"),
    "ui_function_string" = stringr::str_c(.data$name, "_ui")
  )

