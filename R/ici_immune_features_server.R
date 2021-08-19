ici_immune_features_server <- function(
  id
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ici_datasets <- shiny::reactive({
        x <- iatlas.api.client::query_datasets(types = "ici")
        setNames(as.character(x$name), x$display)
      })

      var_choices <- reactive({
        iatlas.api.client::query_features(cohorts = ici_datasets()) %>%
          dplyr::filter(!class %in% c( "Survival Status", "Survival Time")) %>%
          dplyr::select(
                INTERNAL = name,
                DISPLAY = display,
                CLASS = class)
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

      features_df <- shiny::reactive({
        iatlas.api.client::query_feature_values(cohorts = ici_datasets())
      })



      ici_distribution_server(
        "ici_immune_features_distribution",
        ici_datasets(),
        variable_options = var_choices(),
        metadata_feature_df = categories_df(),
        feature_values = features_df()
      )
    }
  )
}
