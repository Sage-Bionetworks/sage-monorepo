germline_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
      titleBox(
        "iAtlas Explorer — Germline Analysis"
      ),
      textBox(
        width = 12,

       # p("Explore the germline genetic contribution to the immune landscape of cancer."),
        p("This module relates to the manuscript: Sayaman et al. Germline genetic contribution to the immune landscape of cancer. bioRxiv 2020.01.30.926527; doi: https://doi.org/10.1101/2020.01.30.926527"),
        p("Explore the germline genetic contribution to the immune landscape of cancer with results of heritability analysis, GWAS (N=9,603), and rare variant analysis (N=9,138) across 30 non-hematological cancer types characterized by the TCGA. All analyses adjusted for cancer type, age at diagnosis, sex, and the first seven components from principal component analysis (PCA) done on SNP data, which largely capture genetic ancestry.")
      ),
      sectionBox(
        title = "Heritability",
        module_ui(ns("germline_heritability"))
      ),
      sectionBox(
        title = "GWAS",
        module_ui(ns("germline_gwas"))
      )
    )


}
