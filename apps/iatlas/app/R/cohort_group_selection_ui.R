cohort_group_selection_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::fluidRow(
    messageBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("cohort_group_selection")),
    ),
    optionsBox(
      width = 12,
      shiny::column(
        width = 12,
        shiny::uiOutput(ns("select_group_ui")),
        shiny::conditionalPanel(
          condition = "output.display_immune_feature_bins",
          shiny::uiOutput(ns("select_immune_feature_bins_group_ui")),
          shiny::sliderInput(
            inputId = ns("bin_number_choice"),
            label = "Select number of bins",
            min = 2,
            max = 10,
            value = 2,
            step = 1
          ),
          ns = ns
        ),
        shiny::conditionalPanel(
          condition = "output.display_driver_mutation",
          shiny::uiOutput(ns("select_driver_mutation_group_ui")),
          ns = ns
        )
      )
    )
  )
}
