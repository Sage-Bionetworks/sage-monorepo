data_table_ui <- function(
    id,
    message_html = "",
    button_text = "Download datatable"
){

    ns <- shiny::NS(id)

    shiny::tagList(
        messageBox(width = 12, message_html),
        shiny::fluidRow(
            tableBox(
                width = 12,
                shiny::div(
                    style = "overflow-x: scroll",
                    "data_table" %>%
                        ns() %>%
                        DT::dataTableOutput(.) %>%
                        shinycssloaders::withSpinner(.)
                )
            ),
            shiny::downloadButton(ns("download_table"), button_text)
        )
    )
}
