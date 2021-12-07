test_that("tumor_microenvironment_cell_proportions_server", {
  shiny::testServer(
    tumor_microenvironment_cell_proportions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      )
    ),
    {
      expect_type(plot_data_function(), "closure")
      expect_type(plot_data_function()(NULL), "list")
    }
  )
})
