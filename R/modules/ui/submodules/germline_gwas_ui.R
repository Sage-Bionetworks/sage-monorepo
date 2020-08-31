germline_gwas_ui <- function(id){

    ns <- shiny::NS(id)
    shiny::tagList(
        iatlas.app::messageBox(
            width = 12,
            shiny::p("GWAS were only performed on 33 immune traits that demonstrated nominally significant heritability (p < 0.05) in at least one ancestry group,
    since these were most likely to have significant genetic effects. "),
            shiny::p("The Manhattan plot below represents the -log10 p of the significant and suggestive GWAS hits by chromosomal position across the 33 immune
traits. Select an Immune Trait of interest to have the GWAS hits associated with this trait highlighted. You can also select a region of interest to narrow down the visualization.
")
        ),
        shiny::column(
          width = 3,
          iatlas.app::optionsBox(
            width = 12,
            shiny::verticalLayout(
              #width = 4,
              shiny::uiOutput(ns("features")),
              shiny::sliderInput(ns("yrange"), "Select range of p-values to be added", min = 6, max = 30, value = c(6,12), step = 1),
              shiny::radioButtons(ns("selection"), "Select range of visualization", choices = c("See all chromossomes", "Select a region"), selected = "See all chromossomes"),
              shiny::conditionalPanel(paste0("input['", ns("selection"), "'] == 'Select a region'"),
                                      numericInput(ns("chr"), "Chromossome", value= 1, min = 1, max = 22, step = 1),
                                      uiOutput(ns("pos_1")),
                                      uiOutput(ns("pos_2")),
                                      uiOutput(ns("link_genome"))
            )
           )
         )
        ),
        shiny::column(
          width = 9,
          iatlas.app::plotBox(
            width = 12,
            plotly::plotlyOutput(ns("mht_plot"), height = "400px") %>%
              shinycssloaders::withSpinner(.)
          )
        ),
        iatlas.app::messageBox(
          width = 12,
          shiny::p("IFN GWAS analysis")
        ),
        shiny::column(
          width = 3,
          iatlas.app::optionsBox(
            width = 12,
            shiny::verticalLayout(
              #width = 4,
              shiny::selectInput(ns("ifnsig"), "Select Interferon Signature", choices = "IFN 21978456"),
              shiny::sliderInput(ns("ifnyrange"), "Select range of p-values to be added", min = 0, max = 1, value = 0.05, step = 0.05),
              shiny::radioButtons(ns("ifnselection"), "Select range of visualization", choices = c("See all chromossomes", "Select a region"), selected = "See all chromossomes"),
              shiny::conditionalPanel(paste0("input['", ns("ifnselection"), "'] == 'Select a region'"),
                                      numericInput(ns("ifnchr"), "Chromossome", value= 1, min = 1, max = 22, step = 1),
                                      uiOutput(ns("ifnpos_1")),
                                      uiOutput(ns("ifnpos_2")),
                                      uiOutput(ns("ifnlink_genome"))
              )
            )
          )
        )
)
}
