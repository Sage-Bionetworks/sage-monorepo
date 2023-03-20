sc_module_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.modules::titleBox(
            "iAtlas Explorer — Single cell data prototype"
        ),
        iatlas.modules::textBox(
            width = 12,
            shiny::p("Prototype of viz of scRNA seq data")
        ),
        iatlas.modules::sectionBox(
            title = "UMAP",
            sc_umap_ui(ns("sc_umap"))
        ),
        iatlas.modules::sectionBox(
            title = "bubble plot",
            sc_bubbleplot_ui(ns("sc_bubbleplot"))
        ),
    )
}
