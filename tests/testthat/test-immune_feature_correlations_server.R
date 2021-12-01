test_that("immune_feature_correlations_server", {
  shiny::testServer(
    immune_feature_correlations_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      )
    ),
    {
      expect_type(feature_classes(), "character")
      expect_type(response_features(), "list")
      expect_type(feature_data_function(), "closure")
      feature_data <- feature_data_function()("Adaptive Receptor - T cell")
      expect_type(feature_data, "list")
      expect_named(
        feature_data,
        c(
          "sample",
          "group",
          "feature",
          "feature_value",
          "feature_order",
          "group_description",
          "color"
        )
      )
      response_data <- response_data_function()("Eosinophils")
      expect_type(response_data, "list")
      expect_named(
        response_data,
        c(
          "sample",
          "feature",
          "feature_value"
        )
      )
      expect_type(summarise_function_list(), "list")
      session$setInputs("heatmap-feature_class_choice" = "Adaptive Receptor - T cell")
      session$setInputs("heatmap-response_feature_choice" = "Eosinophils")
      session$setInputs("heatmap-summarise_function_choice" = "Spearman")
      session$setInputs("heatmap-test_event_data" = data.frame(
        "x" = "C1", "y" = "TCR Richness"
      ))
      expect_type(result$heatmap_data(), "list")
      expect_named(result$heatmap_data(), c('feature', 'C1', 'C2', 'C3', 'C4', 'C6'))
      expect_type(result$scatterplot_data(), "list")
      expect_named(result$scatterplot_data(), c("x", "y", "text"))

    }
  )
})
