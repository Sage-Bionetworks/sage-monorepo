germline_gwas_ui <- function(id){

    ns <- shiny::NS(id)
    shiny::tagList(
        messageBox(
            width = 12,
            shiny::p("GWAS were only performed on 33 immune traits that demonstrated nominally significant heritability (p < 0.05) in at least one ancestry group,
    since these were most likely to have significant genetic effects. "),
            shiny::p("The Manhattan plot below represents the -log10 p of the significant and suggestive GWAS hits by chromosomal position across the 33 immune
traits. Select an Immune Trait of interest to highlight the GWAS hits associated with this trait. You can also select a region of interest to narrow down the visualization."),
            shiny::p("Manuscript context: Figure 3A is reproduced with the 'See all chromossomes' option.
                     Figures 4A can be reproduced by selecting IFN 21978456 in 'Select Immune Features'.
              To generate Figure 4B, change the range of visualization to 'Select a region', Chromossome 2 and then select the coordinates by zooming in the plot or
              by manually updating the start and end of the region of interest. Similar procedures should be followed for
                     Figures 4E, 5B, 5D, S4D, S5A, S5C, S5E.")
        ),
        shiny::column(
          width = 2,
          optionsBox(
            width = 12,
            shiny::verticalLayout(
              shiny::uiOutput(ns("features")),
              shiny::checkboxInput(ns("only_selected"), "Display only selected feature(s)"),
              shiny:: conditionalPanel(
                condition = paste("" , paste0("input['", ns("only_selected"), "'] == false")),
                shiny::uiOutput(ns("to_exclude"))
              ),
              shiny::sliderInput(ns("yrange"), "Select range of -log10(p-values) to be included", min = 6, max = 30, value = c(6,12), step = 1),
              shiny::radioButtons(ns("selection"), "Select range of visualization", choices = c("See all chromossomes", "Select a region"), selected = "See all chromossomes")
           )
         )
        ),
        shiny::column(
          width = 10,
          shiny::conditionalPanel(paste0("input['", ns("selection"), "'] == 'Select a region'"),
                                  messageBox(
                                    width = 12,
                                    # uiOutput(ns("link_genome")),
                                    # shiny::br(),
                                    shiny::p("Update region of interest by zooming in the plot or by manually adding coordinates:"),
                                    shiny::column(
                                      width = 2,
                                      shiny::numericInput(ns("chr"), "Chr", value= 1, min = 1, max = 22, step = 1)
                                    ),
                                    shiny::column(
                                      width = 10,
                                      uiOutput(ns("xrange"))
                                    )
                                  )
          ),
          plotBox(
            width = 12,
            plotly::plotlyOutput(ns("mht_plot"), height = "400px") %>%
              shinycssloaders::withSpinner(.),
            shiny::conditionalPanel(paste0("input['", ns("selection"), "'] == 'Select a region'"),
                                    shiny::plotOutput(ns("genome_plot"), height = "200px") %>%
                                      shinycssloaders::withSpinner(.)
            )
          ),
          plotBox(
            width = 6,
            DT::DTOutput(ns("snp_tbl")),
            shiny::uiOutput(ns("clicked"))
          ),
          plotBox(
            width = 6,
            plotly::plotlyOutput(ns("colocalization")) %>%
              shinycssloaders::withSpinner(.)

          )
        )
)
}
