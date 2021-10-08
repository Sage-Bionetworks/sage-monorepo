test_that("clinical_outcomes_server", {
  shiny::testServer(
    clinical_outcomes_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      )
    ),
    {
      expect_true(T)
    }
  )
})
