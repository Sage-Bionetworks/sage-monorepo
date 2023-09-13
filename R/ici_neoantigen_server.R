ici_neoantigen_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      #getting neoantigen data
      top_mhc_df <- shiny::reactive(iatlasGraphQLClient::query_neoantigens())

      count_df <- shiny::reactive(iatlasGraphQLClient::query_feature_values(feature_classes = "Neoantigen"))

      #vector with dataset labels for easy reference
      dataset_displays <- reactive({
        setNames(cohort_obj()$dataset_displays, cohort_obj()$dataset_names)
      })

      ici_neoantigen_classes_server(
        "ici_neoantigen_classes",
        cohort_obj,
        count_df(),
        dataset_displays()
      )
      ici_neoantigen_correlations_server(
        "ici_neoantigen_correlations",
        cohort_obj,
        count_df(),
        dataset_displays()
      )
      ici_neoantigen_frequency_server(
        "ici_neoantigen_frequency",
        cohort_obj,
        top_mhc_df(),
        dataset_displays()
      )
    }
  )
}
