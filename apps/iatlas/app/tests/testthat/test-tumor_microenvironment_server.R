test_that("tumor_microenvironment_server", {
  shiny::testServer(
    tumor_microenvironment_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
      expect_true(T)
    }
  )
})
