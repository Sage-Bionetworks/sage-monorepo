cohort_dataset_selection_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    messageBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("cohort_dataset_selection")),
      shiny::textOutput(ns("module_availibility_string"))
    ),
    shiny::fluidRow(
      optionsBox(
        width = 12,
        shiny::column(
          width = 12,
          shiny::uiOutput(ns("dataset_selection_ui"))
        )
      ),
    )
  )
}
