germline_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
      titleBox(
        "iAtlas Explorer — Germline Analysis"
      ),
      textBox(
        width = 12,
        p("This module provides interactive visualizations related to the manuscript ",
          a(href = "https://doi.org/10.1016/j.immuni.2021.01.011", "Sayaman et al., Germline genetic contribution to the immune landscape of cancer, Immunity (2021)")),
        p("Explore the germline genetic contribution to the immune landscape of cancer with results of heritability analysis, GWAS, and rare variant analysis across 30 non-hematological cancer types characterized by the TCGA. All analyses are adjusted for cancer type, age at diagnosis, sex, and the first seven components from principal component analysis (PCA) done on SNP data, which capture overall genetic ancestry.")
      ),
      sectionBox(
        title = "Heritability",
        module_ui(ns("germline_heritability"))
      ),
      sectionBox(
        title = "GWAS",
        module_ui(ns("germline_gwas"))
      ),
      sectionBox(
        title = "Rare Variants",
        module_ui(ns("germline_rarevariants"))
      )
    )


}
