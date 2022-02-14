call_iatlas_module <- function(
  name,
  server_function,
  input,
  session,
  ...,
  tab_id = "explorertabs"
){
  link_string     <- paste0("link_to_", name)

  shiny::observeEvent(input[[link_string]], {
    shinydashboard::updateTabItems(
      session,
      inputId = tab_id,
      selected = name
    )
  })
  x <-server_function(name, ...)
  return(x)
}
