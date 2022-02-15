test_that("til_map_distributions_server", {
  shiny::testServer(
    til_map_distributions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      ),
      "mock_event_data" = shiny::reactive(data.frame(
        "curveNumber" = c(0,0),
        "pointNumber" = c(0,0),
        "x" = "C4",
        "y" = c(5.1, 2.1),
        "key" = "TCGA"
      ))
    ),
    {
      session$setInputs("distplot-scale_method_choice" = "None")
      session$setInputs("distplot-reorder_method_choice" = "None")
      session$setInputs("distplot-feature_choice" = "Ball_Hall")
      session$setInputs("distplot-plot_type_choice" = "Violin")


      expect_type(feature_data(), "list")
      expect_named(
        feature_data(),
        c("feature_name", "feature_display", "feature_class")
      )

      expect_type(sample_data_function(), "closure")

      plot_data <- sample_data_function()(.feature = "Ball_Hall")
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
          "dataset_name"
        )
      )
      expect_equal(
        unique(plot_data$feature_display),
        "Ball Hall Index"
      )

      histogram_data <- result()$histogram_data
      expect_type(histogram_data, "list")
      expect_named(histogram_data, "feature_value")
      expect_true(nrow(histogram_data) > 0)
      distplot_data <- result()$distplot_data
      expect_type(distplot_data, "list")
      expect_named(
        distplot_data,
        c(
          'sample_name',
          'group_display',
          'group_color',
          'group_description',
          'feature_display',
          'feature_value',
          'dataset_display'
        )
      )
      expect_true(nrow(distplot_data) > 0)
    }
  )
})
