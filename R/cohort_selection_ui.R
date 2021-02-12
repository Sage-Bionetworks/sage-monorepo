cohort_selection_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    titleBox("iAtlas Explorer — Cohort Selection"),
    textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("cohort_selection1"))
    ),
    sectionBox(
      title = "Cohort Selection",
      messageBox(
        width = 12,
        shiny::includeMarkdown(get_markdown_path("cohort_selection2")),
      ),
      shiny::fluidRow(
        optionsBox(
          width = 12,
          shiny::column(
            width = 12,
            shiny::selectInput(
              inputId = ns("cohort_mode_choice"),
              label   = "Select Cohort Selection Mode",
              choices = c("Cohort Selection", "Cohort Upload")
            )
          )
        )
      ),
      shiny::conditionalPanel(
        condition = "input.cohort_mode_choice == 'Cohort Selection'",
        cohort_manual_selection_ui(ns("cohort_manual_selection")),
        ns = ns
      ),
      shiny::conditionalPanel(
        condition = "input.cohort_mode_choice == 'Cohort Upload'",
        cohort_upload_selection_ui(ns("cohort_upload_selection")),
        ns = ns
      )
    ),
    sectionBox(
      title = "Group Key",
      data_table_ui(
        ns("sg_table"),
        message_html = shiny::p(paste0(
          "This displays attributes and annotations of ",
          "your choice of groups."
        ))
      )
    )
  )
}
