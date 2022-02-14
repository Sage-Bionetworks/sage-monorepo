test_that("immune_feature_distributions_server", {
  shiny::testServer(
    immune_feature_distributions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      )
    ),
    {
      expect_type(features(), "list")
      expect_named(
        features(),
        c("feature_class", "feature_name", "feature_display")
      )
      session$setInputs("distplot-scale_method_choice" = "None")
      session$setInputs("distplot-reorder_method_choice" = "None")
      session$setInputs("distplot-feature_choice" = "age_at_diagnosis")
      session$setInputs("distplot-plot_type_choice" = "Violin")
      session$setInputs("distplot-mock_event_data" = data.frame(
        "curveNumber" = c(0,0),
        "pointNumber" = c(0,0),
        "x" = "C1",
        "y" = c(5.1, 2.1),
        "key" = "C1"
      ))

      expect_type(plot_data_function(), "closure")

      plot_data <- plot_data_function()(.feature = features()$feature_name[[1]])
      expect_type(plot_data, "list")
      expect_named(
        plot_data,
        c(
          "sample_name",
          "group_name",
          "feature_name",
          "feature_display",
          "feature_value",
          "feature_order",
          "group_description",
          "group_color"
        )
      )
      expect_equal(
        unique(plot_data$feature_display),
        features()$feature_display[[1]]
      )

      histogram_data <- result$histogram_data()
      expect_type(histogram_data, "list")
      expect_named(histogram_data, "feature_value")
      expect_true(nrow(histogram_data) > 0)
      distplot_data <- result$distplot_data()
      expect_type(distplot_data, "list")
      expect_named(
        distplot_data,
        c(
          'sample_name',
          'feature_name',
          'feature_value',
          'group_name',
          'group_description',
          'group_color'
        )
      )
      expect_true(nrow(distplot_data) > 0)
    }
  )
})
