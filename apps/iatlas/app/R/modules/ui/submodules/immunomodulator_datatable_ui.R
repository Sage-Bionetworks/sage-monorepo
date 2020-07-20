immunomodulators_datatable_ui <- function(id) {

    ns <- shiny::NS(id)

    source_files <- c(
        "R/modules/ui/submodules/data_table_ui.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    data_table_ui(
        ns("im_table"),
        message_html = shiny::includeMarkdown(
            "markdown/immunomodulators_dt.markdown"
        )
    )
}
