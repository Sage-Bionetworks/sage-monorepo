ici_neoantigen_frequency_ui <- function(
  id,
  cohort_obj
){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      p("The scatterplot shows the frequency of the top 1% most common combinations of MHC, peptide (pMHC), and gene and the number of patients
        who have it. The points are colored based on the grouping criteria selected in the Cohort Selection module."),
      p("Points with high values on both axis represent pMHCs that are frequent (x-axis) and are carried by multiple patients (y-axis),
      and potentially have several mutations events that lead to their appearance.
        On the other hand, points with lower values on both axis represent pMHCs with low frequency that are present in few patients. "),
      p("Click on any point for a table summarizing the frequency of the selection across all groups and selected datasets."),
      shiny::actionLink(ns("method_link"), "Click to view the description of the method for this visualization."),
    ),
    iatlas.modules::optionsBox(
      width = 12,
      shiny::uiOutput(ns("gene_selection"))
    ),
    iatlas.modules::plotBox(
      width = 12,
      shiny::column(
        width = 9,
        shiny::uiOutput(ns("frequency_plot")) %>%
          shinycssloaders::withSpinner(.)
      ),
      shiny::column(
        width = 3,
        div(
          DT::DTOutput(ns("legend")),
          DT::DTOutput(ns("pmhc_tbl")),
          style = "font-size: 70%"
        )
      )
    )
  )
}
