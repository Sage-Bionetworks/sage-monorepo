call_iatlas_module <- function(
  name,
  server_function,
  input,
  session,
  ...,
  tab_id = "explorertabs"
){
  link_string     <- paste0("link_to_", name)
  if (tab_id == "explorertabs") {
    update_function <- shinydashboard::updateTabItems
    tab_name        <- name
  } else if (tab_id == "toolstabs") {
    update_function <- shiny::updateNavlistPanel
    tab_name        <- name %>%
      stringr::str_replace_all("_", " ") %>%
      stringr::str_to_title(.)
  }
  shiny::observeEvent(input[[link_string]], {
    update_function(
      session,
      tab_id,
      tab_name
    )
  })
  server_function(name, ...)
}
