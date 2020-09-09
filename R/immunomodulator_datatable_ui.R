immunomodulators_datatable_ui <- function(id) {

    ns <- shiny::NS(id)

    data_table_ui(
        ns("im_table"),
        message_html = shiny::includeMarkdown(
            "inst/markdown/immunomodulators_dt.markdown"
        )
    )
}
