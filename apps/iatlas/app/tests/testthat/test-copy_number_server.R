test_that("copy_number_server", {
  shiny::testServer(
    copy_number_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
      expect_true(T)
    }
  )
})
