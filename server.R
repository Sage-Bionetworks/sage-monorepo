################################################################################
# Options, default settings, and load packages
################################################################################
# By default, the file size limit is 5MB. It can be changed by
# setting this option. Here we'll raise limit to 9MB.
options(shiny.maxRequestSize = 100 * 1024^2)
options(shiny.usecairo = FALSE)

library(magrittr)

modules_tbl <- MODULES_TBL %>%
  dplyr::mutate(
    "server_function" = purrr::map(.data$server_function_string, get),
  )

analysis_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "analysis")
ici_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "ici")
tool_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "tool")




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


  # Analysis Modules ----------------------------------------------------------

  data_info_server("data_info")

  cohort_obj <- iatlas.modules2::cohort_selection_server("analysis_cohort_selection")

  analysis_modules_tbl %>%
    dplyr::select("name", "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session, cohort_obj)

  shiny::observeEvent(input$link_to_cohort_selection, {
    shinydashboard::updateTabItems(
      session,
      "explorertabs",
      "analysis_cohort_selection"
    )
  })

  # ICI Modules ----------------------------------------------------------

  ici_cohort_obj <- iatlas.modules2::cohort_selection_server(
    "ici_cohort_selection",
    default_datasets = shiny::reactive(c("Gide_Cell_2019", "HugoLo_IPRES_2016")),
    default_group = shiny::reactive("Responder"),
    dataset_type = shiny::reactive("ici")
  )

  ici_modules_tbl %>%
    dplyr::select("name", "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session, ici_cohort_obj)

  shiny::observeEvent(input$link_to_ici_cohort_selection, {
    shinydashboard::updateTabItems(
      session,
      "explorertabs",
      "ici_cohort_selection"
    )
  })

  # Tool Modules --------------------------------------------------------------

  tool_modules_tbl %>%
    dplyr::select("name", "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session)


})


