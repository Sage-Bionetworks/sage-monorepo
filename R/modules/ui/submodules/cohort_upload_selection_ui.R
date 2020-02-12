cohort_upload_selection_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        shiny::fluidRow(
            .GlobalEnv$optionsBox(
                width = 12,
                shiny::tags$head(
                    shiny::tags$script(src = "message-handler.js")
                ),
                shiny::actionButton(
                    ns("filehelp"),
                    "Formatting instructions",
                    icon = shiny::icon("info-circle")
                ),
                shiny::hr(),
                shiny::fileInput(
                    ns("file1"),
                    "Choose CSV File",
                    multiple = FALSE,
                    accept = c(
                        "text/csv",
                        "text/comma-separated-values,text/plain",
                        ".csv"
                    )
                )
            )
        ),
        shiny::fluidRow(
            .GlobalEnv$messageBox(
                width = 12,
                p("After uploading your file, the table below will show your defined groups."),
                DT::dataTableOutput(
                    ns("user_group_df")
                )
            )
        )
    )
}
