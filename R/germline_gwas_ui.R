germline_gwas_ui <- function(id){

    ns <- shiny::NS(id)
    shiny::tagList(
        messageBox(
            width = 12,
            shiny::p("GWAS were performed on 33 immune traits that demonstrated nominally significant heritability (p < 0.05) in at least one ancestry group.  Here you can visualize the significant (p < 10-6) GWAS hits, as a Manhattan plot."),
            shiny::p("Choose Immune Features of interest to select or exclude the GWAS hits associated with trait(s). You can add multiple GWAS tracks, with different selection of immune features, or leave the *Choose Immune Feature(s)* field empty and visualize all available results."),
            shiny::p("More information on a particular SNP, including links to external resources,  can be obtained by clicking on the Manhattan plot, or by searching on the dropdown menu on the right."),
            shiny::p("Manuscript context: For an image similar to Figure 4A, choose 'Select' in the 'Select or Exclude?' menu, select immune feature “IFN 21978456”, and hit 'Add GWAS Track'. Use the IGV navigation bar to select the region of interest."),
            shiny::p("GWAS results can also be visualized in ",
              a(href = "https://pheweb-tcga.qcri.org/about", "Pheweb.")),
            shiny::actionLink(ns("method_link_gwas"), "Click to view method description.")
        ),
        shiny::column(
          width = 9,
          optionsBox(
            width = 12,
            shiny::column(
              width = 3,
              shiny::radioButtons(ns("feature_action"), "Select or Exclude?", choices = c("Select", "Exclude"), selected = "Exclude")
            ),
            shiny::column(
              width = 6,
              shiny::selectizeInput(ns('immunefeature'), "Choose Immune Feature(s)",
                                    choices = NULL,
                                    multiple = TRUE)
            ),
            shiny::column(
              width = 3,
              shiny::br(),
              shiny::actionButton(ns("addGwasTrackButton"), "Add GWAS Track"),
              align = "center"
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
            shiny::p("We conducted a GWAS paired with colocalization analyses, with eQTL and sQTL analyses performed in TCGA and GTEx.
               Results with a Colocalization Posterior Probability (CLPP) > 0.01 are summarized in the tables below."),
            shiny::p("Click on a table row to visualize the plot. The table is updated with available plots in the region displayed at the manhattan plot."),
            shiny::actionLink(ns("method_link_colocalization"), "Click to view method description.")
          ),
          shiny::fluidRow(
          tableBox(
            width = 6,
            div(
              DT::DTOutput(ns("colocalization_tcga")) %>%
                shinycssloaders::withSpinner(.),
              style = "font-size: 75%"),
            shiny::uiOutput(ns("tcga_colocalization_plot"))
            ),
          tableBox(
            width = 6,
            div(
              DT::DTOutput(ns("colocalization_gtex")) %>%
                shinycssloaders::withSpinner(.),
              style = "font-size: 75%"),
            shiny::uiOutput(ns("gtex_colocalization_plot"))
            )
          )
)
}
