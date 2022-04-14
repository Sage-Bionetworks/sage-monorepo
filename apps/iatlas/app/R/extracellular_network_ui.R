extracellular_network_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.modules::titleBox(
            "iAtlas Explorer — Extracellular Networks"
        ),
        iatlas.modules::textBox(
            width = 12,
            shiny::includeMarkdown(get_markdown_path(
              "extracellular_network"
            ))
        ),
        iatlas.modules::sectionBox(
            title = "Extracellular networks",
            module_ui(ns("extracellular_network_main"))
        )
    )
}
