germline_gwas_ui <- function(id){

    ns <- shiny::NS(id)
    shiny::tagList(
        messageBox(
            width = 12,
            shiny::p("GWAS were only performed on 33 immune traits that demonstrated nominally significant heritability (p < 0.05) in at least one ancestry group,
    since these were most likely to have significant genetic effects. "),
            shiny::p("The Manhattan plot below represents the -log10 p of the significant and suggestive GWAS hits by chromosomal position across the 33 immune
traits. Select an Immune Trait of interest to highlight the GWAS hits associated with this trait. You can also select a region of interest to narrow down the visualization."),
            shiny::p("Manuscript context: Figure 3A is reproduced with the 'See all chromosomes' option.
                     Figures 4A can be reproduced by selecting IFN 21978456 in 'Select Immune Features'.
              To generate Figure 4B, change the range of visualization to 'Select a region', Chromosome 2 and then select the coordinates by zooming in the plot or
              by manually updating the start and end of the region of interest. Similar procedures should be followed for
                     Figures 4E, 5B, 5D, S4D, S5A, S5C, S5E."),
            shiny::actionLink(ns("method_link_gwas"), "Click to view method description.")
        ),
        shiny::column(
          width = 9,
          optionsBox(
            width = 12,
            shiny::column(
              width = 5,
              shiny::selectizeInput(ns('immunefeature'), "Exclude Immune Feature (optional)",
                                    choices = NULL,
                                    multiple = TRUE),
              shiny::radioButtons(ns("feature_action"), "Select or Exclude?", choices = c("Select", "Exclude"), selected = "Exclude")
            ),
            shiny::column(
              width = 4,
              actionButton(ns("addGwasTrackButton"), "Add GWAS Track")
            )
         ),
         iatlas.app::plotBox(
           width = 12,
           igvShiny::igvShinyOutput(ns('igv_plot')) %>%
             shinycssloaders::withSpinner(.)
         )
        ),
        shiny::column(
          width = 3,
          shiny::verticalLayout(
            iatlas.app::optionsBox(
              width = 12,
              shiny::uiOutput(ns("search_snp"))
            ),
            iatlas.app::messageBox(
              width = 12,
              shiny::uiOutput(ns("links"))
            ),
            tableBox(
              width = 12,
              DT::DTOutput(ns("snp_tbl"))
             )
            )
           ),
          iatlas.app::messageBox(
            width = 12,
            shiny::p("We conducted a GWAS paired with colocalization analyses, and below you can access the results.
                     eQTL and sQTL analyses were performed in TCGA and GTEx. The table in the left summarizes the TCGA results, and contains two types of plots: three level plots and expanded region. The table at the right summarises the GTEX results, and is updated with changes in the chromosome selected in the manhattan plot above."
                     ),
            shiny::actionLink(ns("method_link_colocalization"), "Click to view method description.")
          )#,
          #shiny::fluidRow(
            # column(
            #   width = 6,
            #   tableBox(
            #     width = 12,
            #     DT::DTOutput(ns("colocalization_tcga")) %>%
            #       shinycssloaders::withSpinner(.),
            #     shiny::uiOutput(ns("tcga_colocalization_plot"))
            #   )
            # ),
            # column(
            #   width = 6,
            #   tableBox(
            #     width = 12,
            #     DT::DTOutput(ns("colocalization_gtex")) %>%
            #       shinycssloaders::withSpinner(.),
            #     shiny::uiOutput(ns("gtex_colocalization_plot"))
            #   )
            # )
          #)
)
}
