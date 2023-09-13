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

      # #adding warning for user of dataset selected in ICI Cohort Selection that doesn't have neoantigen data in iAtlas
      output$excluded_dataset <- shiny::renderText({
        if(any(!cohort_obj()$sample_tbl$sample_name %in% unique(count_df()$sample))){
          absent_samples <- cohort_obj()$sample_tbl %>%
            dplyr::filter(!sample_name %in% count_df()$sample) %>%
            dplyr::group_by(dataset_name) %>%
            dplyr::summarise(n_missing = dplyr::n_distinct(sample_name)) %>%
            dplyr::mutate(text = glue::glue("<li>Dataset {dataset_displays()[dataset_name]} has {n_missing} samples not included in this module."))

          paste(
            "<ul><i> There are samples selected in ICI Cohort Selection that do not have neoantigen data present in this module: </i><br>",
            paste(absent_samples$text, collapse = "</li>"),
            "</ul>"
          )
        }else{
          ""
        }
      })

      ici_neoantigen_classes_server(
        "ici_neoantigen_classes",
        cohort_obj,
        count_df(),
        shiny::reactive(dataset_displays())
      )
      ici_neoantigen_correlations_server(
        "ici_neoantigen_correlations",
        cohort_obj,
        count_df(),
        shiny::reactive(dataset_displays())
      )
      ici_neoantigen_frequency_server(
        "ici_neoantigen_frequency",
        cohort_obj,
        top_mhc_df(),
        shiny::reactive(dataset_displays())
      )
    }
  )
}
