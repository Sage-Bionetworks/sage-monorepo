test_that("cellimage_main_server", {
  shiny::testServer(
    cellimage_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj)
    ),
    {
      expect_true(T)
    }
  )
})
