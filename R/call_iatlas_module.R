call_iatlas_module <- function(
    file,
    input,
    session,
    ...,
    observe_event = T,
    tab_id = "explorertabs"
){
    function_string <- stringr::str_remove(basename(file), ".R")
    module_string   <- stringr::str_remove(basename(file), "_server.R")
    link_string     <- paste0("link_to_", module_string)
    source(file, local = T)
    if (observe_event) {
        if (tab_id == "explorertabs") {
            update_function <- shinydashboard::updateTabItems
            tab_name        <- module_string
        } else if (tab_id == "toolstabs") {
            update_function <- shiny::updateNavlistPanel
            tab_name        <- module_string %>%
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
    }

    get(function_string)(module_string, ...)
}
