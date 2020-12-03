test_that("immunomodulator_distributions_server", {
  shiny::testServer(
    immunomodulator_distributions_server,
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
          "Gene Family",
          "Gene Function",
          "Immune Checkpoint",
          "Super Category"
        )
      )
      expect_type(plot_data_function(), "closure")
    }
  )
})
