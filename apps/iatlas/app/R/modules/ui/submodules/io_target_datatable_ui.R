io_target_datatable_ui <- function(id) {

    ns <- shiny::NS(id)

    source_files <- c(
        "R/modules/ui/submodules/data_table_ui.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    data_table_ui(
        ns("datatable"),
        message_html = shiny::includeMarkdown(
            "markdown/io_target_dt.markdown"
        )
    )
}
