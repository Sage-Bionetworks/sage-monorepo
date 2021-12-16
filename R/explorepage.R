explorepage_ui <- function(){

  # module tables ----
  modules_tbl <- MODULES_TBL %>%
    dplyr::mutate(
      "ui_function" = purrr::map(.data$ui_function_string, get)
    )

  ici_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "ici")
  cg_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "cg")
  tool_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "tool")

  # sidebar ----

  create_menu_subitems <- function(tbl){
    tbl %>%
      dplyr::select("text" = "display", "tabName" = "name") %>%
      purrr::pmap(shinydashboard::menuSubItem, icon = shiny::icon("cog"))
  }

  ici_module_menu_items   <- create_menu_subitems(ici_modules_tbl)
  cg_module_menu_items    <- create_menu_subitems(cg_modules_tbl)
  tool_module_menu_items  <- create_menu_subitems(tool_modules_tbl)

  sidebar <- shinydashboard::dashboardSidebar(
    shinydashboard::sidebarMenu(
      id = "explorertabs",
      shinydashboard::menuItem(
        "iAtlas Explorer Home",
        tabName = "dashboard",
        icon = shiny::icon("tachometer-alt")
      ),
      shinydashboard::menuItem(
        "ICI Cohort Selection",
        tabName = "ici_cohort_selection",
        icon = shiny::icon("cog")
      ),
      shinydashboard::menuItem(
        text = "ICI Analysis Modules",
        icon = shiny::icon("chart-bar"),
        startExpanded = TRUE,
        ici_module_menu_items
      ),
      shinydashboard::menuItem(
        "CG Cohort Selection",
        tabName = "cg_cohort_selection",
        icon = shiny::icon("cog")
      ),
      shinydashboard::menuItem(
        text = "CG Analysis Modules",
        icon = shiny::icon("chart-bar"),
        startExpanded = TRUE,
        cg_module_menu_items
      ),
      shinydashboard::menuItem(
        text = "iAtlas tools",
        icon = shiny::icon("wrench"),
        startExpanded = TRUE,
        tool_module_menu_items
      ),
      shinydashboard::menuItem(
        "Data Description",
        icon = shiny::icon("th-list"),
        tabName = "data_info"
      )
    )
  )

  # body ----

  # info boxes at top of page
  readout_info_boxes <- dplyr::tibble(
    title = c(
      "Immune Checkpoint Inhibitors (ICI) datasets:",
      "Cancer Genomics (CG) datasets:",
      "Immune Readouts:",
      "Samples:"
    ),
    value = c(
      nrow(iatlas.api.client::query_datasets(types = "ici")),
      2,
      nrow(iatlas.api.client::query_features()),
      nrow(iatlas.api.client::query_samples())
    ),
    icon = purrr::map(c("search", "database", "filter", "users"), shiny::icon)
  ) %>%
    purrr::pmap(
      shinydashboard::infoBox,
      width = 6,
      color = "black",
      fill = FALSE
    )

  # image boxes at bottom of page that link to module tabs

  make_image_boxes <- function(tbl){
    tbl %>%
      dplyr::select(
        "title" = "display",
        "linkId" = "link",
        "imgSrc" = "image",
        "boxText" = "description"
      ) %>%
      purrr::pmap(
        iatlas.modules::imgLinkBox, width = 6, linkText = "Open Module"
      ) %>%
      dplyr::tibble("item" = .) %>%
      dplyr::mutate("row" = as.character(ceiling(dplyr::row_number() / 2))) %>%
      dplyr::group_by(.data$row) %>%
      dplyr::mutate("n" = as.character(dplyr::row_number())) %>%
      dplyr::ungroup() %>%
      tidyr::pivot_wider(names_from = "n", values_from = "item") %>%
      dplyr::select("item1" = "1", "item2" = "2") %>%
      dplyr::mutate("row" = purrr::map2(
        .data$item1,
        .data$item2,
        shiny::fluidRow
      )) %>%
      dplyr::pull("row")
  }

  cg_module_image_boxes  <- make_image_boxes(cg_modules_tbl)
  ici_module_image_boxes <- make_image_boxes(ici_modules_tbl)

  # This is the tab item that users land on
  landing_tab_item <- list(shinydashboard::tabItem(
    tabName = "dashboard",
    iatlas.modules::titleBox("iAtlas Explorer - Home"),
    iatlas.modules::sectionBox(
      title = "What's Inside",
      shiny::column(
        width = 6,
        shiny::includeMarkdown("inst/markdown/explore1.markdown")
      ),
      shiny::column(
        width = 6,
        shiny::verticalLayout(
          readout_info_boxes[1:2],
          readout_info_boxes[3:4]
        )
      )
    ),
    iatlas.modules::sectionBox(
      title = "Get Started",
      iatlas.modules::textBox(
        width = 6,
        shiny::p(shiny::h4(shiny::strong("1. Build your Cohort"))),
        shiny::p("Use our cohort selector to explore the available data and narrow down your research targets."),
        shiny::splitLayout(
          shiny::actionButton("link_to_ici_cohort_selection", label = "Open ICI Cohort Selection"),
          shiny::actionButton(inputId = "link_to_cg_cohort_selection", label = "Open CG Cohort Selection")
        )
      ),
      iatlas.modules::textBox(
        width = 6,
        shiny::p(shiny::h4(shiny::strong("2. Visualize your data"))),
        shiny::p("Use our analysis modules to explore the selected cohorts. You can access the analysis modules from the sections below and from the left menu. Any changes in the selected cohort in step 1 will be automatically propagated to the corresponding modules.")
      )
    ),
    iatlas.modules::sectionBox(
      title = "Immune Checkpoint Inhibition Analysis Modules",
      iatlas.modules::messageBox(
        width = 12,
        shiny::includeMarkdown("inst/markdown/explore3.markdown")
      ),
      ici_module_image_boxes
    ),
    iatlas.modules::sectionBox(
      title = "Cancer Genomics Analysis Modules",
      iatlas.modules::messageBox(
        width = 12,
        shiny::includeMarkdown("inst/markdown/explore2.markdown")
      ),
      cg_module_image_boxes
    )
  ))

  # These tabs are the result of calling the ui function of each module
  module_tab_items <-
    dplyr::bind_rows(
      dplyr::select(modules_tbl, "label", "ui_function"),
      dplyr::tibble(
        "label" = c(
          "cg_cohort_selection",
          "ici_cohort_selection",
          "data_info"
        ),
        "ui_function" = c(
          iatlas.modules2::cohort_selection_ui,
          iatlas.modules2::cohort_selection_ui,
          data_info_ui
        )
      )
    ) %>%
    purrr::pmap(~shinydashboard::tabItem(tabName = .x, .y(.x)))

  body <-
    do.call(shinydashboard::tabItems, c(landing_tab_item, module_tab_items)) %>%
    shinydashboard::dashboardBody()

  # dashboard page ----
  shinydashboard::dashboardPage(
    header  = shinydashboard::dashboardHeader(disable = TRUE),
    sidebar = sidebar,
    body = body
  )
}
