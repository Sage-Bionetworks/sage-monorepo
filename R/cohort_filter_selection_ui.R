cohort_filter_selection_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    messageBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("cohort_filter_selection")),
      shiny::textOutput(ns("samples_text"))
    ),
    shiny::fluidRow(
      optionsBox(
        width = 12,
        shiny::column(
          width = 12,
          insert_remove_element_ui(
            ns("group_filter"),
            "Add group filter"
          )
        )
      ),
      optionsBox(
        width = 12,
        shiny::column(
          width = 12,
          insert_remove_element_ui(
            ns("numeric_filter"),
            "Add numeric filter"
          )
        )
      )
    )
  )
}
