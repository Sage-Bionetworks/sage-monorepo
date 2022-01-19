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

ici_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "ici")
cg_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "cg")
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

  # ICI Modules ----------------------------------------------------------

  ici_cohort_obj <- call_iatlas_module(
    "ici_cohort_selection",
    iatlas.modules2::cohort_selection_server,
    input,
    session,
    default_datasets = shiny::reactive(c("Gide_Cell_2019", "HugoLo_IPRES_2016")),
    default_group = shiny::reactive("Responder"),
    dataset_type = shiny::reactive("ici"),
    display_module_availibility_string = shiny::reactive(F)
  )

  ici_modules_tbl %>%
    dplyr::select("name", "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session, ici_cohort_obj)

  # CG Modules ----------------------------------------------------------

  cg_cohort_obj <- call_iatlas_module(
    "cg_cohort_selection",
    iatlas.modules2::cohort_selection_server,
    input,
    session
  )

  cg_modules_tbl %>%
    dplyr::select("name", "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session, cg_cohort_obj)

  # Tool Modules --------------------------------------------------------------

  tool_modules_tbl %>%
    dplyr::select("name", "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session)

  # Other ---------------------------------------------------------------------

  call_iatlas_module(
    "data_info",
    data_info_server,
    input,
    session
  )

})


