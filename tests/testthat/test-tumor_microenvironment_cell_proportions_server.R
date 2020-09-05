test_that("module_works", {
  shiny::testServer(
    tumor_microenvironment_cell_proportions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj)
    ),
    {
      expect_type(output$barplot, "character")
      expect_error(output$scatterplot, class = "shiny.silent.error")
    }
  )
})
