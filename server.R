################################################################################
# Options, default settings, and load packages
################################################################################
# By default, the file size limit is 5MB. It can be changed by
# setting this option. Here we'll raise limit to 9MB.
options(shiny.maxRequestSize = 100 * 1024^2)
options(shiny.usecairo = FALSE)

library(magrittr)

modules_tbl <- readr::read_tsv("module_config.tsv")


################################################################################
# Begin Shiny Server definition.
################################################################################
shiny::shinyServer(function(input, output, session) {

  shiny::observe({
    query <- shiny::parseQueryString(session$clientData$url_search)
    if (!is.null(query[['module']])) {
      shinydashboard::updateTabItems(
        session,
        "explorertabs",
        query[['module']]
      )
    }
  })

  # Explore page modules ------------------------------------------------------

  cohort_obj <- call_iatlas_module(
    "cohort_selection",
    "cohort_selection_server",
    input,
    session
  )

  call_iatlas_module(
    "data_info",
    "data_info_server",
    input,
    session
  )

  # Analysis modules --------------------------------------------------------

  modules_tbl %>%
    dplyr::filter(.data$type == "analysis") %>%
    dplyr::select("name", "function_string" = "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session, cohort_obj)

  # Tool modules --------------------------------------------------------

  modules_tbl %>%
    dplyr::filter(.data$type == "tool") %>%
    dplyr::select("name", "function_string" = "server_function") %>%
    purrr::pwalk(
      iatlas.app::call_iatlas_module, input, session, tab_id = "toolstabs"
    )
})


