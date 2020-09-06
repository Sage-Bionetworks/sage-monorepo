extracellular_network_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::titleBox(
            "iAtlas Explorer — Extracellular Networks"
        ),
        iatlas.app::textBox(
            width = 12,
            paste0(
                "Explore the extracellular networks modulating tumoral immune ",
                "response, encompassing direct interaction among cells and ",
                "communication via soluble proteins such as cytokines to ",
                "mediate interactions among those cells. ",
                "This module uses the network of documented ligand-receptor, ",
                "cell-receptor, and cell-ligand pairs published by ",
                "Ramilowski et al., 2015 and retrieved from FANTOM5."
            )
        ),
        iatlas.app::sectionBox(
            title = "Extracellular networks",
            module_ui(ns("extracellular_network_main"))
        )
    )
}
