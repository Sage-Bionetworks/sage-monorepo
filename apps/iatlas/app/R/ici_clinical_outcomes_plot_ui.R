ici_clinical_outcomes_plot_ui <- function(id){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      shiny::includeMarkdown("inst/markdown/ici_clinical_outcomes.markdown")
    ),
    iatlas.modules::optionsBox(
      width=12,
      shiny::column(
        width = 8,
        shiny::selectInput(
          ns("timevar"),
          "Survival Endpoint",
          c("Overall Survival" = "OS_time",
            "Progression Free Interval" = "PFI_time_1"),
          selected = "OS_time"
        )
      ),
      shiny::column(
        width = 2,
        shiny::checkboxInput(
          ns("confint"),
          "Confidence Intervals",
          value = F
        )
      ),
      shiny::column(
        width = 2,
        shiny::checkboxInput(
          ns("risktable"),
          "Risk Table",
          value = T
        )
      )
    ),#optionsBox
    shiny::column(
      width = 12,
      shiny::htmlOutput(ns("notification")),
      shiny::htmlOutput(ns("excluded_dataset")),
      iatlas.modules::plotBox(
        width = 12,
        shiny::uiOutput(ns("plots")) %>%
          shinycssloaders::withSpinner()
      )
    )
  )
}
