################################################################################
# Options, default settings, and load packages
################################################################################
# By default, the file size limit is 5MB. It can be changed by
# setting this option. Here we'll raise limit to 9MB.
options(shiny.maxRequestSize = 100 * 1024^2)
options(shiny.usecairo = FALSE)

library(magrittr)

modules_tbl <- dplyr::tribble(
  ~path,                                  ~type,
  "R/cellimage_server.R",                 "analysis",
  "R/clinical_outcomes_server.R",         "analysis",
  "R/copy_number_server.R",               "analysis",
  "R/driver_associations_server.R",       "analysis",
  "R/extracellular_network_server.R",     "analysis",
  "R/immune_features_server.R",           "analysis",
  "R/immunomodulators_server.R",          "analysis",
  "R/io_targets_server.R",                "analysis",
  "R/til_maps_server.R",                  "analysis",
  "R/tumor_microenvironment_server.R",    "analysis",
  "R/immune_subtype_classifier_server.R", "tool"
)

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

  # Non analysis modules -----------------------------------------------------

  cohort_obj <- call_iatlas_module(
    "R/cohort_selection_server.R",
    input,
    session
  )

  call_iatlas_module(
    "R/data_info_server.R",
    input,
    session,
    observe_event = F
  )

  # Analysis modules --------------------------------------------------------
  modules_tbl %>%
    dplyr::filter(.data$type == "analysis") %>%
    dplyr::pull("path") %>%
    purrr::walk(iatlas.app::call_iatlas_module, input, session, cohort_obj)

  # Tool modules --------------------------------------------------------

  modules_tbl %>%
    dplyr::filter(.data$type == "tool") %>%
    dplyr::pull("path") %>%
    purrr::walk(
      iatlas.app::call_iatlas_module, input, session, tab_id = "toolstabs"
    )
})


