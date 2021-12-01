test_that("til_map_server", {
  shiny::testServer(
    til_map_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      )
    ),
    {
      expect_true(shiny::is.reactive(show_submodule))
      expect_type(show_submodule(), "closure")
      expect_true(show_submodule()(cohort_obj()))
    }
  )
})

