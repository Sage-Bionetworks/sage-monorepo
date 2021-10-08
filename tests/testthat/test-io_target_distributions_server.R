test_that("io_target_distributions_server", {
  shiny::testServer(
    io_target_distributions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      )
    ),
    {
      expect_true(is.na(url_gene()))
      expect_null(default_gene())

      expect_type(features(), "list")
      expect_named(
        features(),
        c(
          "feature_name",
          "feature_display",
          "Pathway",
          "Therapy Type"
        )
      )
      expect_type(plot_data_function(), "closure")
      entrez = as.character(features()$feature_name[[1]])

      plot_data <- plot_data_function()(.feature = entrez)
      expect_type(plot_data, "list")
      expect_named(
        plot_data,
        c(
          "sample",
          "group",
          "feature",
          "feature_value",
          "group_description",
          "color"
        )
      )
      expect_true(nrow(plot_data) > 0)
      expect_equal(unique(plot_data$feature), features()$feature_display[[1]])
    }
  )
})
