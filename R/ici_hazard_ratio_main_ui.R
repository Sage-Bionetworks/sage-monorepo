ici_hazard_ratio_main_ui <- function(id){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      shiny::includeMarkdown("inst/markdown/ici_hazard_ratio.markdown")
    ),
    iatlas.modules::optionsBox(
      width = 12,
      column(
        width = 4,
        selectInput(
          ns("analysisvar"),
          "Select Type of Analysis",
          c("Univariable Cox Proportional" = "uni_coxph",
            "Multivariable Cox Proportional" = "mult_coxph"
          ),
          selected = "uni_coxph"
        ),
        actionLink(ns("method_link"), "Click to view method description for each type.")
      ),
      column(
        width = 3,
        selectInput(
          ns("timevar"),
          "Survival Endpoint",
          c("Overall Survival" = "OS_time",
            "Progression Free Interval" = "PFI_time_1"),
          selected = "OS_time"
        )
      ),
      column(
        width = 5,
        shiny::selectizeInput(
                      ns("var2_cox"),
                      label = "Select or Search for variables",
                      choices = NULL,
                      multiple = TRUE
                    ),
        shiny::actionButton(ns("go_button"), "Compute HR")
      )
    ),
    shiny::htmlOutput(ns("notification")),
    shiny::htmlOutput(ns("excluded_dataset")),
    iatlas.modules::plotBox(
      width = 12,
      plotly::plotlyOutput(ns("mult_heatmap"), width = "100%", height = "600px")%>%
        shinycssloaders::withSpinner()
    ),
    iatlas.modules::plotBox(
      width = 12,
      plotly::plotlyOutput(ns("mult_forest"), width = "100%", height = "700px")%>%
        shinycssloaders::withSpinner()
    ),
    iatlas.modules::messageBox(
      width = 12,
      p("The table shows the computed data used to create the visualizations above.")
    ),
    iatlas.modules::tableBox(
      width = 12,
      DT::dataTableOutput(ns("stats_summary")),
      downloadButton(ns('download_stats'), 'Download')
    )
  )#taglist
}
