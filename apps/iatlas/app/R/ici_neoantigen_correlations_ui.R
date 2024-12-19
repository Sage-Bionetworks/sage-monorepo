ici_neoantigen_correlations_ui <- function(
  id,
  cohort_obj
){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      p("Select a class of immune features and a type of neoantigen.
      The selection will generate a heatmap summarizing the correlation between the frequency of neoantigens and immune features values, for each dataset and groups selected in ICI Cohort Selection.
       Click on the heatmap for a scatter plot with the immune features values and the frequency of neoantigen origin for the selected dataset and group. For information about distributions of immune features, refer to the Immune Features module."),
      shiny::actionLink(ns("method_link"), "Click to view the description of the method for computing the correlation.")
    ),
    iatlas.modules::optionsBox(
      width = 24,
      shiny::column(
        width = 5,
        shiny::uiOutput(ns("class_selection_ui"))
      ),
      shiny::column(
        width = 4,
        shiny::uiOutput(ns("neoantigen_selection_ui"))
      ),
      shiny::column(
        width = 3,
        shiny::uiOutput(ns("summarise_function_ui"))
      )
    ),
    iatlas.modules::plotBox(
      width = 24,
      plotly::plotlyOutput(ns("cor_plot"), width = "100%", height = "600") %>%
        shinycssloaders::withSpinner(.)
    ),
    iatlas.modules::plotBox(
      width = 24,
      plotly::plotlyOutput(ns("scatter_plot"), width = "100%", height = "300")
    )
  )
}
