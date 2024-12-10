test_that("immune_feature_correlations_server", {
  shiny::testServer(
    immune_feature_correlations_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      ),
      "mock_event_data" = shiny::reactive(data.frame(
        "curveNumber" = 0,
        "pointNumber" = 1,
        "x" = "C1",
        "y" = "TCR Richness",
        "z" = "0.1805093"
      ))
    ),
    {
      session$setInputs("heatmap-class_choice" = "Adaptive Receptor - T cell")
      session$setInputs("heatmap-response_choice" = "Eosinophils")
      session$setInputs("heatmap-summarise_function_choice" = "Spearman")

      feature_sample_data <- feature_sample_data_function()(
        "Adaptive Receptor - T cell"
      )
      expect_true(tibble::is_tibble(feature_sample_data))
      expect_named(
        feature_sample_data,
        c(
          'sample_name',
          'group_name',
          'feature_name',
          'feature_display',
          'feature_value',
          'feature_order',
          'dataset_name'
        )
      )

      response_sample_data <- response_sample_data_function()("Eosinophils")
      expect_true(tibble::is_tibble(response_sample_data))
      expect_named(
        response_sample_data,
        c(
          "sample_name",
          "feature_name",
          "feature_value"
        )
      )

      expect_true(tibble::is_tibble(feature_data()))
      expect_named(
        feature_data(),
        c(
          "feature_name",
          "feature_display",
          "feature_class",
          "feature_order"
        )
      )

      expect_true(tibble::is_tibble(response_data()))
      expect_named(
        response_data(),
        c(
          "feature_name",
          "feature_display",
          "feature_class"
        )
      )


      expect_type(feature_sample_data_function(), "closure")
      expect_type(summarise_function_list(), "list")

      expect_type(result()$heatmap_data, "list")
      expect_named(
        result()$heatmap_data,
        c('feature_display', 'C1', 'C2', 'C3', 'C4', 'C6')
      )
      expect_type(result()$scatterplot_data, "list")
      expect_named(result()$scatterplot_data, c("x", "y", "text"))


    }
  )
})
