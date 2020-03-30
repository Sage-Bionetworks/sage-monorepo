til_maps_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/til_map_distributions_ui.R", local = T)
    source("R/modules/ui/submodules/call_module_ui.R", local = T)
    source("R/modules/ui/submodules/til_map_datatable_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — TIL Maps"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/tilmap.markdown")
        ),
        .GlobalEnv$sectionBox(
            title = "Distributions",
            call_module_ui(
                ns("til_map_distributions"),
                til_map_distributions_ui
            )
        ),
        .GlobalEnv$sectionBox(
            title = "TIL Map Annotations",
            call_module_ui(
                ns("til_map_datatable"),
                til_map_datatable_ui
            )
        )
    )
}
