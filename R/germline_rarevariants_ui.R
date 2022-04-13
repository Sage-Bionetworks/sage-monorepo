germline_rarevariants_ui <- function(id){

  ns <- shiny::NS(id)
  shiny::tagList(
    iatlas.modules::messageBox(
      width = 12,
      shiny::p("The boxplots show the values of selected immune traits across samples with germline mutations in genes belonging to defined functional categories, or pathways."),
      shiny::p("We performed association analyses between germline pathogenic and likely pathogenic cancer predisposition variants in high penetrance susceptibility genes, and immune traits and immune
               subtypes.  Since mutations in most of the genes were rare we collapsed genes into categories summarizing different biologic processes or functions, when possible."),
      shiny::actionLink(ns("method_link"), "Click to view method description.")
    ),
    iatlas.modules::optionsBox(
      width = 12,
      shiny::column(
        width = 8,
        shiny::selectizeInput(ns("feature"),
                           "Search and select Immune Trait",
                           choices = NULL,
                           multiple = FALSE)
      ),
      shiny::column(
        width = 4,
        shiny::selectizeInput(ns("order_box"),
                              "Order plots by ",
                              choices = list(
                                             "p-value" = "p_value",
                                             "Median" = "q2",
                                             "Mean" = "mean",
                                             "Min" = "min",
                                             "Max" = "max",
                                             "Number of patients with mutation" = "n_mutants"
                              ),
                              selected = "p_value")
      )
    ),
    iatlas.modules::plotBox(
      width = 12,
      plotly::plotlyOutput(ns("dist_plot"), height = "700px") %>%
        shinycssloaders::withSpinner(.)
    ),
    iatlas.modules::messageBox(
      width = 3,
      shiny::p("Tests comparing the phenotype values with respect to the burden of rare variants within each pathway was performed (i.e., mean of phenotype values of rare mutation carriers vs mean of phenotype values of rare mutation non-carriers).
              In the Pathway column, 'Multiple' refers to samples with mutation in more than one pathway; and 'No defect' refers to samples with no mutation in the studied pathways.")
    ),
    iatlas.modules::plotBox(
      width = 9,
      DT::dataTableOutput(ns("stats_tbl"))
    )

  )
}
