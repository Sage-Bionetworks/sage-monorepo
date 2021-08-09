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
        # ioresponse_data$feature_df %>%
        #   dplyr::filter(VariableType == "Numeric" & `Variable Class` != "NA") %>%
        #   dplyr::select(
        #     INTERNAL = FeatureMatrixLabelTSV,
        #     DISPLAY = FriendlyLabel,
        #     CLASS = `Variable Class`)

        iatlas.api.client::query_features(cohorts = ici_datasets()) %>%
          dplyr::filter(!class %in% c( "Survival Status", "Survival Time")) %>%
          dplyr::select(
                INTERNAL = name,
                DISPLAY = display,
                CLASS = class)
      })



      ici_distribution_server(
        "ici_immune_features_distribution",
        ioresponse_data,
        variable_options = var_choices(),
        metadata_feature_df = ioresponse_data$feature_df,
        feature_values = ioresponse_data$fmx_df
      )
    }
  )
}
