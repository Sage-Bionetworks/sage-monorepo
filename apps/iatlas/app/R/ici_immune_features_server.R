ici_immune_features_server <- function(
  id
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      var_choices <- reactive({
        ioresponse_data$feature_df %>%
          dplyr::filter(VariableType == "Numeric" & `Variable Class` != "NA") %>%
          dplyr::select(
            INTERNAL = FeatureMatrixLabelTSV,
            DISPLAY = FriendlyLabel,
            CLASS = `Variable Class`)
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
