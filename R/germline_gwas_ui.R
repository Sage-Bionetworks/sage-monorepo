germline_gwas_ui <- function(id){

    ns <- shiny::NS(id)
    shiny::tagList(
        iatlas.modules::messageBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/germline_gwas.markdown"),
            shiny::p("GWAS results can also be visualized in ",
              a(href = "https://pheweb-tcga.qcri.org/about", "Pheweb.")),
            shiny::actionLink(ns("method_link_gwas"), "Click to view method description.")
        ),
        shiny::column(
          width = 9,
          iatlas.modules::optionsBox(
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
         iatlas.modules::plotBox(
           width = 12,
           igvShiny::igvShinyOutput(ns('igv_plot'), height = NULL) %>%
             shinycssloaders::withSpinner(.)
         )
        ),
        shiny::column(
          width = 3,
          shiny::verticalLayout(
              iatlas.modules::optionsBox(
              width = 12,
              shiny::selectInput(ns("snp_int"), "Click on the plot or search for a SNP id:",
                                choices = NULL)
              #shiny::uiOutput(ns("search_snp"))
            ),
            iatlas.modules::messageBox(
              width = 12,
              shiny::uiOutput(ns("links"))
            ),
            iatlas.modules::tableBox(
              width = 12,
              DT::DTOutput(ns("snp_tbl"))
             )
            )
           ),
        iatlas.modules::messageBox(
            width = 12,
            shiny::p("We conducted a GWAS paired with colocalization analyses, with eQTL and sQTL analyses performed in TCGA and GTEx.
               Results with a Colocalization Posterior Probability (CLPP) > 0.01 are summarized in the tables below."),
            shiny::p("Click on a table row to visualize the plot."),
            shiny::actionLink(ns("method_link_colocalization"), "Click to view method description.")
          ),
          shiny::fluidRow(
              iatlas.modules::tableBox(
            width = 6,
            div(
              DT::DTOutput(ns("colocalization_tcga")) %>%
                shinycssloaders::withSpinner(.),
              style = "font-size: 75%"),
            shiny::uiOutput(ns("tcga_colocalization_plot"))
            ),
            iatlas.modules::tableBox(
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
