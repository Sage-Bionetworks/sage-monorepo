
# system files ----------------------------------------------------------------

get_markdown_path <- function(name, extension = ".markdown"){
  name %>%
    stringr::str_c(extension) %>%
    file.path(system.file("markdown", package = "iatlas.app"), .)
}

get_example_path <- function(name, extension = ".csv"){
  name %>%
    stringr::str_c(extension) %>%
    file.path(system.file("examples", package = "iatlas.app"), .)
}

get_javascript_path <- function(name, extension = ".js"){
  name %>%
    stringr::str_c(extension) %>%
    file.path(system.file("javascript", package = "iatlas.app"), .)
}

get_html_path <- function(name, extension = ".html"){
  name %>%
    stringr::str_c(extension) %>%
    file.path(system.file("html_files", package = "iatlas.app"), .)
}

get_tsv_path <- function(name, extension = ".tsv"){
  name %>%
    stringr::str_c(extension) %>%
    file.path(system.file("tsv", package = "iatlas.app"), .)
}

get_svg_path <- function(name, extension = ".svg"){
  name %>%
    stringr::str_c(extension) %>%
    file.path(system.file("svg", package = "iatlas.app"), .)
}
