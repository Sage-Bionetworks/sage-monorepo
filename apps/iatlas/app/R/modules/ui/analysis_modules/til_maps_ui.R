til_maps_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::titleBox("iAtlas Explorer — TIL Maps"),
        iatlas.app::textBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/tilmap.markdown")
        ),
        iatlas.app::sectionBox(
            title = "Distributions",
            module_ui(ns("til_map_distributions"))
        ),
        iatlas.app::sectionBox(
            title = "TIL Map Annotations",
            module_ui(ns("til_map_datatable"))
        )
    )
}
