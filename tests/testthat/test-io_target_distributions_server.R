test_that("io_target_distributions_server", {
  shiny::testServer(
    io_target_distributions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      ),
      "mock_event_data" = shiny::reactive(data.frame(
        "curveNumber" = c(0,0),
        "pointNumber" = c(0,0),
        "x" = "C1",
        "y" = c(5.1, 2.1),
        "key" = "PCAWG"
      ))
    ),
    {
      session$setInputs("distplot-feature_choice" = "340273")
      session$setInputs("distplot-scale_method_choice" = "None")
      session$setInputs("distplot-reorder_method_choice" = "None")
      session$setInputs("distplot-plot_type_choice" = "Violin")

      expect_true(is.na(url_gene()))
      expect_null(default_gene())

      expect_true(tibble::is_tibble(feature_data()))
      expect_named(
        feature_data(),
        c(
          "feature_name",
          "feature_display",
          "Pathway",
          "Therapy Type"
        )
      )
      expect_type(sample_data_function(), "closure")
      entrez = as.character(feature_data()$feature_name[[1]])

      sample_data <- sample_data_function()(.feature = entrez)
      expect_true(tibble::is_tibble(sample_data))
      expect_named(
        sample_data,
        c(
          "sample_name",
          "group_name",
          "feature_name",
          "feature_display",
          "feature_value",
          "dataset_name"
        )
      )
      expect_true(nrow(sample_data) > 0)
      expect_equal(
        unique(sample_data$feature_display),
        feature_data()$feature_display[[1]]
      )

      expect_type(result(), "list")
    }
  )
})
