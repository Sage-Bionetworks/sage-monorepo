til_map_datatable_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/data_table_ui.R", local = T)

    shiny::tagList(
        data_table_ui(
            ns("til_table"),
            message_html = shiny::includeMarkdown(
                "markdown/tilmap_table.markdown"
            )
        )
    )
}
