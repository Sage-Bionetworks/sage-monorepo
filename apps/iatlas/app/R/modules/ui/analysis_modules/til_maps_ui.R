til_maps_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/til_map_distributions_ui.R", local = T)
    source("R/modules/ui/submodules/til_map_datatable_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — TIL Maps"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/tilmap.markdown")
        ),

        til_map_distributions_ui(ns("til_map_distributions")),
        til_map_datatable_ui(ns("til_map_datatable"))
    )
}
