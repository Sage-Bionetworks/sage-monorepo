################################################################################
# Options, default settings, and load packages
################################################################################
# By default, the file size limit is 5MB. It can be changed by
# setting this option. Here we'll raise limit to 9MB.
options(shiny.maxRequestSize = 100 * 1024^2)
options(shiny.usecairo = FALSE)

library(magrittr)

modules_tbl <- readr::read_tsv("module_config.tsv")
analysis_modules_tbl <- dplyr::filter(modules_tbl, .data$type == "analysis")



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

  # Analysis modules ----------------------------------------------------------

  analysis_modules_tbl %>%
    dplyr::select("name", "function_string" = "server_function") %>%
    purrr::pwalk(iatlas.app::call_iatlas_module, input, session, cohort_obj)

  # Tool modules --------------------------------------------------------------

  modules_tbl %>%
    dplyr::filter(.data$type == "tool") %>%
    dplyr::select("name", "function_string" = "server_function") %>%
    purrr::pwalk(
      iatlas.app::call_iatlas_module, input, session, tab_id = "toolstabs"
    )

  # Sidebar menu --------------------------------------------------------------
  output$sidebar_menu <- shinydashboard::renderMenu({
    shinydashboard::sidebarMenu(
      id = "explorertabs",
      shinydashboard::menuItem(
        "iAtlas Explorer Home",
        tabName = "dashboard",
        icon = shiny::icon("dashboard")
      ),
      shinydashboard::menuItem(
        "Cohort Selection",
        tabName = "cohort_selection",
        icon = shiny::icon("cog")
      ),
      shinydashboard::menuItem(
        "Data Description",
        icon = shiny::icon("th-list"),
        tabName = "data_info"
      ),
      shinydashboard::menuItem(
        text = "Analysis Modules",
        icon = shiny::icon("bar-chart"),
        startExpanded = TRUE,
        purrr::map2(
          modules_tbl$display,
          modules_tbl$name,
          ~ shinydashboard::menuSubItem(
            text = .x,
            tabName = .y,
            icon = shiny::icon("cog")
          )
        )
      )
    )
  })

  # Dashboard Body ------------------------------------------------------------

  output$dashboard_body <- shiny::renderUI({
    list1 <- list(
    # tab item 1 -----
      shinydashboard::tabItem(
        tabName = "dashboard",
        iatlas.app::titleBox("iAtlas Explorer — Home"),
        iatlas.app::textBox(
          width = 12,
          shiny::includeMarkdown("inst/markdown/explore.markdown")
        ),
        iatlas.app::sectionBox(
          title = "What's Inside",
          shiny::fluidRow(
            shinydashboard::infoBox(
              "Immune Readouts:",
              86,
              width = 3,
              color = "black",
              fill = FALSE,
              icon = shiny::icon("search")
            ),
            shinydashboard::infoBox(
              "Classes of Readouts:",
              12,
              width = 3,
              color = "black",
              fill = FALSE,
              icon = shiny::icon("filter")
            ),
            shinydashboard::infoBox(
              "TCGA Cancers:",
              33,
              width = 3,
              color = "black",
              fill = FALSE,
              icon = shiny::icon("flask")
            ),
            shinydashboard::infoBox(
              "TCGA Samples:",
              "11,080",
              width = 3,
              color = "black",
              fill = FALSE,
              icon = shiny::icon("users")
            )
          )
        ),
        iatlas.app::sectionBox(
          title = "Analysis Modules",
          iatlas.app::messageBox(
            width = 12,
            shiny::p(
              "Each module presents information organized by theme, with multiple views and interactive controls.",
              "Within each module, you can find ",
              shiny::strong("“Manuscript Context”"), " describing how that module can generate figures analogous to those in the manuscript ",
              shiny::em("Thorsson et al., The Immune Landscape of Cancer, Immunity (2018).")
            )
          ),
          shiny::fluidRow(
            iatlas.app::imgLinkBox(
              width = 6,
              linkId = "link_to_cohort_selection",
              title = "Cohort Selection",
              imgSrc = "images/cohort_selection.png",
              boxText = "Use this module to create a cohort of interest.",
              linkText = "Open Module"
            ),
            iatlas.app::imgLinkBox(
              width = 6,
              linkId = "link_to_tumor_microenvironment",
              title = "Tumor Microenvironment",
              imgSrc = "images/tumor_microenvironment.png",
              boxText = "Explore the immune cell proportions in your sample groups.",
              linkText = "Open Module"
            )
          ),
          shiny::fluidRow(
            iatlas.app::imgLinkBox(
              width = 6,
              title = "Immune Feature Trends",
              linkId = "link_to_immune_features",
              imgSrc = "images/immune_features.png",
              boxText = "This module allows you to see how immune readouts vary across your groups, and how they relate to one another.",
              linkText = "Open Module"
            ),
            iatlas.app::imgLinkBox(
              width = 6,
              linkId = "link_to_clinical_outcomes",
              title = "Clinical Outcomes",
              imgSrc = "images/clinical_outcomes.png",
              boxText = "Plot survival curves based on immune characteristics and identify variables associated with outcome.",
              linkText = "Open Module"
            )
          ),
          shiny::fluidRow(
            iatlas.app::imgLinkBox(
              width = 6,
              title = "Immunomodulators",
              linkId = "link_to_immunomodulators",
              imgSrc = "images/immunomodulators.png",
              boxText = "Explore the expression of genes that code for immunomodulating proteins, including checkpoint proteins.",
              linkText = "Open Module"
            ),
            iatlas.app::imgLinkBox(
              width = 6,
              title = "TIL Maps",
              linkId = "link_to_til_maps",
              imgSrc = "images/til_maps.png",
              boxText = "Explore the characteristics of maps of tumor infiltrating lymphocytes obtained from analysis of H&E images.",
              linkText = "Open Module"
            )
          ),
          shiny::fluidRow(
            iatlas.app::imgLinkBox(
              width = 6,
              title = "Driver Associations",
              linkId = "link_to_driver_associations",
              imgSrc = "images/driver_associations.png",
              boxText = "Explore Associations of Microenvironment with Driver Mutations.",
              linkText = "Open Module"
            ),
            iatlas.app::imgLinkBox(
              width = 6,
              title = "IO Targets",
              linkId = "link_to_io_targets",
              imgSrc = "images/io_targets.png",
              boxText = "Explore the expression of genes that code for immuno-oncological (IO) targets .",
              linkText = "Open Module"
            )
          ),
          shiny::fluidRow(
            iatlas.app::imgLinkBox(
              width = 6,
              title = "CNV Associations",
              linkId = "link_to_copy_number",
              imgSrc = "images/copy_number.png",
              boxText = "Explore associations of microenvironment with gene copy number.",
              linkText = "Open Module"
            ),
            iatlas.app::imgLinkBox(
              width = 6,
              title = "Extracellular Networks",
              linkId = "link_to_extracellular_network",
              imgSrc = "images/extracellular_network.png",
              boxText = "Explore the extracellular networks modulating tumoral immune response.",
              linkText = "Open Module"
            )
          ),
          shiny::fluidRow(
            iatlas.app::imgLinkBox(
              width = 6,
              title = "Cell-Interaction Diagram",
              linkId = "link_to_cellimage",
              imgSrc = "images/cellimage.png",
              boxText = "Explore cell and protein abundance on an illustration.",
              linkText = "Open Module"
            )
          )
        )
      )
    )
    # item 2----
    list2 <- list(
      shinydashboard::tabItem(
        tabName = "cohort_selection",
        cohort_selection_ui("cohort_selection")
      ),
      shinydashboard::tabItem(
        tabName = "data_info",
        data_info_ui("data_info")
      )
    )

    list3 <- list()
    for(i in 1:nrow(analysis_modules_tbl)){
      x <- shinydashboard::tabItem(
        tabName = analysis_modules_tbl$name[[i]],
        get(analysis_modules_tbl$ui_function[[i]])(analysis_modules_tbl$name[[i]])
      )
      list3[[i]] <- x
    }

    do.call(
      shinydashboard::tabItems,
      purrr::flatten(list(list1, list2, list3))
    )
  })

})


