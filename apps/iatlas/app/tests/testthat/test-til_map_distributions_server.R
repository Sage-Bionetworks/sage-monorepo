test_that("til_map_distributions_server", {
  shiny::testServer(
    til_map_distributions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      )
    ),
    {
      session$setInputs("distplot-scale_method_choice" = "None")
      session$setInputs("distplot-reorder_method_choice" = "None")
      session$setInputs("distplot-feature_choice" = "Ball_Hall")
      session$setInputs("distplot-plot_type_choice" = "Violin")
      session$setInputs("distplot-mock_event_data" = data.frame(
        "curveNumber" = c(0,0),
        "pointNumber" = c(0,0),
        "x" = "C4",
        "y" = c(5.1, 2.1),
        "key" = "C4"
      ))

      expect_type(features(), "list")
      expect_named(
        features(),
        c("feature_class", "feature_name", "feature_display")
      )

      expect_type(plot_data_function(), "closure")

      plot_data <- plot_data_function()(.feature = "Ball_Hall")
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
        "Ball Hall Index"
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
