test_that("io_target_distributions_server", {
  shiny::testServer(
    io_target_distributions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
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
    }
  )
})
