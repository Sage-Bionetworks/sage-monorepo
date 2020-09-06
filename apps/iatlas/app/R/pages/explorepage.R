modules_tbl <- dplyr::tribble(
  ~path,                              ~type,      ~display,
  "R/cellimage_ui.R",                 "analysis", "Cell-Interaction Diagram",
  "R/clinical_outcomes_ui.R",         "analysis", "Clinical Outcomes",
  "R/copy_number_ui.R",               "analysis", "CNV Associations",
  "R/driver_associations_ui.R",       "analysis", "Driver Associations",
  "R/extracellular_network_ui.R",     "analysis", "Extracellular Networks",
  "R/immune_features_ui.R",           "analysis", "Immune Feature Trends",
  "R/immunomodulators_ui.R",          "analysis", "Immunomodulators",
  "R/io_targets_ui.R",                "analysis", "IO Targets",
  "R/til_maps_ui.R",                  "analysis", "TIL Maps",
  "R/tumor_microenvironment_ui.R",    "analysis", "Tumor Microenvironment"
)

explorepage <- shinydashboard::dashboardPage(
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
        "Analysis Modules",
        icon = shiny::icon("bar-chart"),
        startExpanded = TRUE,
        list(
        shinydashboard::menuSubItem(
          "Tumor Microenvironment",
          tabName = "tumor_microenvironment",
          icon = shiny::icon("cog")
        ),
        shinydashboard::menuSubItem(
          "Immune Feature Trends",
          tabName = "immune_features",
          icon = shiny::icon("cog")
        ),
        shinydashboard::menuSubItem(
          "Clinical Outcomes",
          tabName = "clinical_outcomes",
          icon = shiny::icon("cog")
        ),
        shinydashboard::menuSubItem(
          "Immunomodulators",
          tabName = "immunomodulators",
          icon = shiny::icon("cog")
        ),
        shinydashboard::menuSubItem(
          "IO Targets",
          tabName = "io_targets",
          icon = shiny::icon("cog")
        ),
        shinydashboard::menuSubItem(
          "TIL Maps",
          tabName = "til_maps",
          icon = shiny::icon("cog")
        ),
        shinydashboard::menuSubItem(
          "Driver Associations",
          tabName = "driver_associations",
          icon = shiny::icon("cog")
        ),
        shinydashboard::menuSubItem(
          "CNV Associations",
          tabName = "copy_number",
          icon = shiny::icon("cog")
        ),
        shinydashboard::menuSubItem(
          "Extracellular Networks",
          tabName = "extracellular_network",
          icon = shiny::icon("cog")
        ),
        shinydashboard::menuSubItem(
          "Cell-Interaction Diagram",
          tabName = "cellimage",
          icon = shiny::icon("cog")
        )
        )
      )
    )
  ),
  body = shinydashboard::dashboardBody(
    shinydashboard::tabItems(
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
      ),
      shinydashboard::tabItem(
        tabName = "tumor_microenvironment",
        tumor_microenvironment_ui("tumor_microenvironment")
      ),
      shinydashboard::tabItem(
        tabName = "cohort_selection",
        cohort_selection_ui("cohort_selection")
      ),
      shinydashboard::tabItem(
        tabName = "clinical_outcomes",
        clinical_outcomes_ui("clinical_outcomes")
      ),
      shinydashboard::tabItem(
        tabName = "immunomodulators",
        immunomodulators_ui("immunomodulators")
      ),
      shinydashboard::tabItem(
        tabName = "immune_features",
        immune_features_ui("immune_features")
      ),
      shinydashboard::tabItem(
        tabName = "driver_associations",
        driver_associations_ui("driver_associations")
      ),
      shinydashboard::tabItem(
        tabName = "til_maps",
        til_maps_ui("til_maps")
      ),
      shinydashboard::tabItem(
        tabName = "io_targets",
        io_targets_ui("io_targets")
      ),
      shinydashboard::tabItem(
        tabName = "copy_number",
        copy_number_ui("copy_number")
      ),
      shinydashboard::tabItem(
        tabName = "extracellular_network",
        extracellular_network_ui("extracellular_network")
      ),
      shinydashboard::tabItem(
        tabName = "cellimage",
        cellimage_ui("cellimage")
      ),
      shinydashboard::tabItem(
        tabName = "data_info",
        data_info_ui("data_info")
      )
    )
  )
)
