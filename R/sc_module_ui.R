sc_module_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.modules::titleBox(
            "iAtlas Explorer — UMAP Viewer of Single cell RNA-Seq data"
        ),
        iatlas.modules::textBox(
            width = 12,
            shiny::p("Explore UMAP visualizations of single-cell RNA-Seq datasets. Links for visualizations on CELLxGENE are provided for further exploration.")
        ),
        iatlas.modules::sectionBox(
            title = "UMAP",
            sc_umap_ui(ns("sc_umap"))
        )
    )
}
