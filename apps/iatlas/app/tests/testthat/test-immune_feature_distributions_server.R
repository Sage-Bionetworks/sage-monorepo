test_that("immune_feature_distributions_server", {
  shiny::testServer(
    immune_feature_distributions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(get_tcga_immune_subtype_cohort_obj_50())
    ),
    {
      expect_type(features(), "list")
      expect_named(
        features(),
        c("feature_class", "feature_name", "feature_display")
      )
      expect_type(plot_data_function(), "closure")
    }
  )
})
