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
      expect_type(plot_data_function(), "closure")

      plot_data <- plot_data_function()(.feature = features()$feature_name[[1]])
      expect_type(plot_data, "list")
      expect_named(
        plot_data,
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
      expect_equal(unique(plot_data$feature), features()$feature_display[[1]])
    }
  )
})
