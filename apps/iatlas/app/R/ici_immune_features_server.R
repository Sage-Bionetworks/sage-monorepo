ici_immune_features_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ici_datasets <- shiny::reactive({
        x <- iatlas.api.client::query_datasets(types = "ici")
        setNames(as.character(x$name), x$display)
      })

      categories_df <- shiny::reactive(iatlas.api.client::query_tags(datasets = ici_datasets()) %>%
                                         dplyr::mutate(class = dplyr::case_when(
                                           tag_name %in% c( "Response", "Responder", "Progression", "Clinical_Benefit") ~ "Response to ICI",
                                           TRUE ~ "Treatment Data")) %>%
                                         create_nested_list_by_class(.,
                                                                     class_column = "class",
                                                                     internal_column = "tag_name",
                                                                     display_column = "tag_short_display")
      )

      ici_distribution_server(
        "ici_immune_features_distribution",
        cohort_obj,
        metadata_feature_df = categories_df(),
        feature_df <- cohort_obj()$feature_tbl
      )
    }
  )
}
