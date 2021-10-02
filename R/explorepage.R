explorepage_ui <- function(){

  modules_tbl <- MODULES_TBL %>%
    dplyr::mutate(
      "ui_function" = purrr::map(.data$ui_function_string, get)
    )

  analysis_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "analysis")
  ici_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "ici")
  tool_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "tool")

  analysis_module_menu_items <- analysis_modules_tbl %>%
    dplyr::select("text" = "display", "tabName" = "name") %>%
    purrr::pmap(shinydashboard::menuSubItem, icon = shiny::icon("cog"))

  ici_module_menu_items <- ici_modules_tbl %>%
    dplyr::select("text" = "display", "tabName" = "name") %>%
    purrr::pmap(shinydashboard::menuSubItem, icon = shiny::icon("cog"))

  tool_module_menu_items <- tool_modules_tbl %>%
    dplyr::select("text" = "display", "tabName" = "name") %>%
    purrr::pmap(shinydashboard::menuSubItem, icon = shiny::icon("cog"))


  shinydashboard::dashboardPage(
    header  = shinydashboard::dashboardHeader(disable = TRUE),
    sidebar = shinydashboard::dashboardSidebar(
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
          analysis_module_menu_items
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
          ici_module_menu_items
        ),
        shinydashboard::menuItem(
          text = "iAtlas tools",
          icon = shiny::icon("wrench"),
          startExpanded = TRUE,
          tool_module_menu_items
        )
      )
    ),
    body = shinydashboard::dashboardBody({
      # ----
      readout_info_boxes <- dplyr::tibble(
          title = c(
            "Immune Readouts:",
            "Classes of Readouts:",
            "TCGA Cancers:",
            "TCGA Samples:"
          ),
          value = c(
            nrow(iatlas.api.client::query_features()),
            length(unique(iatlas.api.client::query_features()$class)),
            nrow(iatlas.api.client::query_tags(
              datasets = "TCGA", parent_tags = "TCGA_Study"
            )),
            11080
          ),
          icon = purrr::map(c("search", "filter", "flask", "users"), shiny::icon)
        ) %>%
          purrr::pmap(
            shinydashboard::infoBox,
            width = 3,
            color = "black",
            fill = FALSE
          )

      # ----
      module_image_boxes <- {
        lst <- purrr::pmap(
          list(
            title  = analysis_modules_tbl$display,
            linkId = analysis_modules_tbl$link,
            imgSrc = analysis_modules_tbl$image,
            boxText = analysis_modules_tbl$description
          ),
          iatlas.modules::imgLinkBox,
          width = 6,
          linkText = "Open Module"
        )

        row_tbl <- lst %>%
          dplyr::tibble("item" = .) %>%
          dplyr::mutate("row" = as.character(ceiling(dplyr::row_number() / 2))) %>%
          dplyr::group_by(.data$row) %>%
          dplyr::mutate("n" = as.character(dplyr::row_number())) %>%
          dplyr::ungroup() %>%
          tidyr::pivot_wider(names_from = "n", values_from = "item")

        func <- function(i, tbl){
          item1 <- tbl$`1`[[i]]
          item2 <- tbl$`2`[[i]]
          if(is.null(item2)){
            row_list <- shiny::tagList(item1)
          } else {
            row_list <- shiny::tagList(item1, item2)
          }
          shiny::fluidRow(row_list)
        }

        purrr::map(1:nrow(row_tbl), func, row_tbl)
      }

      # ----
      module_tab_items <- modules_tbl %>%
        dplyr::select("label", "ui_function") %>%
        purrr::pmap(~shinydashboard::tabItem(tabName = .x, .y(.x)))


      tab_item <- list(shinydashboard::tabItem(
        tabName = "dashboard",
        iatlas.modules::titleBox("iAtlas Explorer — Home"),
        iatlas.modules::textBox(
          width = 12,
          shiny::includeMarkdown("inst/markdown/explore1.markdown")
        ),
        iatlas.modules::sectionBox(
          title = "What's Inside",
          shiny::fluidRow(readout_info_boxes)
        ),
        iatlas.modules::sectionBox(
          title = "Analysis Modules",
          iatlas.modules::messageBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/explore2.markdown")
          ),
          module_image_boxes
        )
      ))

      do.call(
        shinydashboard::tabItems,
        c(tab_item, module_tab_items)
      )
    })
  )
}
