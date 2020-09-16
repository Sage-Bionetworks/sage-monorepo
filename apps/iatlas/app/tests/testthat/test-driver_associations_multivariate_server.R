test_that("multivariate_driver_server", {
  shiny::testServer(
    multivariate_driver_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
    }
  )
})
