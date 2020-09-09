germline_gwas_ui <- function(id){

    ns <- shiny::NS(id)
    shiny::tagList(
        messageBox(
            width = 12,
            shiny::p("GWAS were only performed on 33 immune traits that demonstrated nominally significant heritability (p < 0.05) in at least one ancestry group,
    since these were most likely to have significant genetic effects. "),
            shiny::p("The Manhattan plot below represents the -log10 p of the significant and suggestive GWAS hits by chromosomal position across the 33 immune
traits. Select an Immune Trait of interest to highlight the GWAS hits associated with this trait. You can also select a region of interest to narrow down the visualization.
")
        ),
        shiny::column(
          width = 2,
          optionsBox(
            width = 12,
            shiny::verticalLayout(
              shiny::uiOutput(ns("features")),
              shiny::sliderInput(ns("yrange"), "Select range of -log10(p-values) to be included", min = 6, max = 30, value = c(6,12), step = 1),
              shiny::radioButtons(ns("selection"), "Select range of visualization", choices = c("See all chromossomes", "Select a region"), selected = "See all chromossomes"),
              shiny::conditionalPanel(paste0("input['", ns("selection"), "'] == 'Select a region'"),
                                      numericInput(ns("chr"), "Chromossome", value= 1, min = 1, max = 22, step = 1)#,
                                      # p("Select Range"),
                                      # uiOutput(ns("range"))

            )
           )
         )
        ),
        shiny::column(
          width = 10,
          plotBox(
            width = 12,
            plotly::plotlyOutput(ns("mht_plot"), height = "400px") %>%
              shinycssloaders::withSpinner(.)
          ),
          shiny::conditionalPanel(paste0("input['", ns("selection"), "'] == 'Select a region'"),
                                  messageBox(
                                    width = 12,
                                    uiOutput(ns("link_genome")),
                                    uiOutput(ns("xrange"))
                                  )
          )
        )
)
}
