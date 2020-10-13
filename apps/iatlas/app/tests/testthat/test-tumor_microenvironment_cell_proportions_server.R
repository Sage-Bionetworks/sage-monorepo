test_that("module_works", {
  shiny::testServer(
    tumor_microenvironment_cell_proportions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
      expect_type(plot_data_function(), "closure")
      expect_type(plot_data_function()(NULL), "list")
    }
  )
})
