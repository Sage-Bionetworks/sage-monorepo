test_that("copy_number_server", {
  shiny::testServer(
    copy_number_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj
      )
    ),
    {
      expect_true(shiny::is.reactive(show_submodule))
      expect_type(show_submodule(), "closure")
      expect_true(show_submodule()(cohort_obj()))
    }
  )
})
