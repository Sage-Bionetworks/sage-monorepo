test_that("immunomodulator_distributions_server", {
  shiny::testServer(
    immunomodulator_distributions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      )
    ),
    {
      session$setInputs("disttplot-distplot-mock_event_data" = data.frame(
        "curveNumber" = c(0,0),
        "pointNumber" = c(0,0),
        "x" = "C1",
        "y" = c(5.1, 2.1),
        "key" = "PCAWG"
      ))
      expect_type(feature_data(), "list")
      expect_named(
        feature_data(),
        c(
          "feature_name",
          "feature_display",
          "Gene Family",
          "Gene Function",
          "Immune Checkpoint",
          "Super Category"
        )
      )
      expect_type(sample_data_function(), "closure")
      entrez = as.character(feature_data()$feature_name[[1]])

      plot_data <- sample_data_function()(.feature = entrez)
      expect_type(plot_data, "list")
      expect_named(
        plot_data,
        c(
          "sample_name",
          "group_name",
          "feature_name",
          "feature_display",
          "feature_value",
          "group_description",
          "group_color",
          "dataset_name"
        )
      )
      expect_equal(unique(plot_data$dataset_name), "PCAWG")
      expect_true(nrow(plot_data) > 0)
      expect_equal(
        unique(plot_data$feature_display),
        feature_data()$feature_display[[1]]
      )
      expect_error(result(), "fff")
    }
  )
})

