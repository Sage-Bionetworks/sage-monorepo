
# system files ----------------------------------------------------------------

get_system_path_file <- function(
  prefix, extension, folder, package = "iatlas.app"
){
  file_name <- stringr::str_c(prefix, extension)
  file.path(system.file(folder, package = package), file_name)
}

get_markdown_path <- function(name, extension = ".markdown"){
  get_system_path_file(name, extension, "markdown")
}

get_example_path <- function(name, extension = ".csv"){
  get_system_path_file(name, extension, "examples")
}

get_javascript_path <- function(name, extension = ".js"){
  get_system_path_file(name, extension, "javascript")
}

get_html_path <- function(name, extension = ".html"){
  get_system_path_file(name, extension, "html_files")
}

get_tsv_path <- function(name, extension = ".tsv"){
  get_system_path_file(name, extension, "tsv")
}

get_svg_path <- function(name, extension = ".svg"){
  get_system_path_file(name, extension, "svg")
}
