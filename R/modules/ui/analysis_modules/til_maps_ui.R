til_maps_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/til_map_distributions_ui.R", local = T)
    source("R/modules/ui/submodules/data_table_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — TIL Maps"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/tilmap.markdown")
        ),

        til_map_distributions_ui(ns("til_map_distributions")),
        data_table_ui(
            ns("til_table"),
            title = "TIL Map Annotations",
            message_html = shiny::includeMarkdown(
                "markdown/tilmap_table.markdown"
            )
        )
    )
}
