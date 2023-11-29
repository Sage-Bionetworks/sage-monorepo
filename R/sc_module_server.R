sc_module_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      # sc_bubbleplot_server(
      #   "sc_bubbleplot",
      #   cohort_obj
      # )

      sc_umap_server(
        "sc_umap",
        cohort_obj
      )
    }
  )
}



