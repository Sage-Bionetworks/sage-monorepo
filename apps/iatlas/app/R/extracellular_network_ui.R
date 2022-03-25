extracellular_network_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlasModules::titleBox(
            "iAtlas Explorer — Extracellular Networks"
        ),
        iatlasModules::textBox(
            width = 12,
            shiny::includeMarkdown(get_markdown_path(
              "extracellular_network"
            ))
        ),
        iatlasModules::sectionBox(
            title = "Extracellular networks",
            module_ui(ns("extracellular_network_main"))
        )
    )
}
