io_target_datatable_ui <- function(id) {

    ns <- shiny::NS(id)

    data_table_ui(
        ns("datatable"),
        message_html = shiny::includeMarkdown(
            "inst/markdown/io_target_dt.markdown"
        )
    )
}
