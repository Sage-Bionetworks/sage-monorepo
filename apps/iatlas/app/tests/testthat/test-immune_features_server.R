test_that("immune_features_server", {
  shiny::testServer(
    immune_features_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj
      )
    ),
    {
      expect_true(T)
    }
  )
})
