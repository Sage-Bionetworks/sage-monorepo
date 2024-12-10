test_that("immunomodulators_server", {
  shiny::testServer(
    immunomodulators_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      )
    ),
    {
      expect_true(T)
    }
  )
})
