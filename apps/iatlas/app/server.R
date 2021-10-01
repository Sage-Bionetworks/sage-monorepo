################################################################################
# Options, default settings, and load packages
################################################################################
# By default, the file size limit is 5MB. It can be changed by
# setting this option. Here we'll raise limit to 9MB.
options(shiny.maxRequestSize = 100 * 1024^2)
options(shiny.usecairo = FALSE)

library(magrittr)

modules_tbl <- "module_config" %>%
  get_tsv_path() %>%
  readr::read_tsv(.) %>%
  dplyr::mutate(
    "label" = dplyr::if_else(.data$label == "none", .data$name, .data$label),
    "link" = stringr::str_c("link_to_", .data$name),
    "image" = stringr::str_c("images/", .data$name, ".png"),
    "server_function_string" = stringr::str_c(.data$name, "_server"),
    "ui_function_string" = stringr::str_c(.data$name, "_ui"),
    "server_function" = purrr::map(.data$server_function_string, get),
    "ui_function" = purrr::map(.data$ui_function_string, get)
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

  cohort_obj <- cohort_selection_server("analysis_cohort_selection")

  analysis_modules_tbl %>%
    dplyr::select("name", "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session, cohort_obj)

  # ICI Modules ----------------------------------------------------------

  cohort_selection_server(
    "ici_cohort_selection",
    default_datasets = shiny::reactive(c("Gide_Cell_2019", "HugoLo_IPRES_2016")),
    default_group = shiny::reactive("Responder"),
    dataset_type = shiny::reactive("ici")
  )

  ici_modules_tbl %>%
    dplyr::select("name", "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session, ici_cohort_obj)

  # Tool Modules --------------------------------------------------------------

  tool_modules_tbl %>%
    dplyr::select("name", "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session)

  # Sidebar Menu --------------------------------------------------------------

  analysis_module_menu_items <- shiny::reactive({
    analysis_modules_tbl %>%
      dplyr::select("text" = "display", "tabName" = "name") %>%
      purrr::pmap(shinydashboard::menuSubItem, icon = shiny::icon("cog"))
  })

  ici_module_menu_items <- shiny::reactive({
    ici_modules_tbl %>%
      dplyr::select("text" = "display", "tabName" = "name") %>%
      purrr::pmap(shinydashboard::menuSubItem, icon = shiny::icon("cog"))
  })

  tool_module_menu_items <- shiny::reactive({
    tool_modules_tbl %>%
      dplyr::select("text" = "display", "tabName" = "name") %>%
      purrr::pmap(shinydashboard::menuSubItem, icon = shiny::icon("cog"))
  })

  output$sidebar_menu <- shinydashboard::renderMenu({
    shinydashboard::sidebarMenu(
      id = "explorertabs",
      shinydashboard::menuItem(
        "iAtlas Explorer Home",
        tabName = "dashboard",
        icon = shiny::icon("dashboard")
      ),
      shinydashboard::menuItem(
        "Data Description",
        icon = shiny::icon("th-list"),
        tabName = "data_info"
      ),
      shinydashboard::menuItem(
        "Cohort Selection",
        tabName = "analysis_cohort_selection",
        icon = shiny::icon("cog")
      ),
      shinydashboard::menuItem(
        text = "Analysis Modules",
        icon = shiny::icon("bar-chart"),
        startExpanded = TRUE,
        analysis_module_menu_items()
      ),
      shinydashboard::menuItem(
        "ICI Cohort Selection",
        tabName = "ici_cohort_selection",
        icon = shiny::icon("cog")
      ),
      shinydashboard::menuItem(
        text = "ICI Modules",
        icon = shiny::icon("bar-chart"),
        startExpanded = TRUE,
        ici_module_menu_items()
      ),
      shinydashboard::menuItem(
        text = "iAtlas tools",
        icon = shiny::icon("wrench"),
        startExpanded = TRUE,
        tool_module_menu_items()
      )
    )
  })



})


