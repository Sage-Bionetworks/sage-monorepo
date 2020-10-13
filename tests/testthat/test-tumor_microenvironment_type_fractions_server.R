test_that("tumor_microenvironment_type_fractions_server_tcga", {
  shiny::testServer(
    tumor_microenvironment_type_fractions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
      expect_type(feature_classes(), "character")
      expect_type(plot_data_function(), "closure")
    }
  )
})

test_that("tumor_microenvironment_type_fractions_server_pcawg", {
  shiny::testServer(
    tumor_microenvironment_type_fractions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(pcawg_immune_subtype_cohort_obj)
    ),
    {
      expect_type(feature_classes(), "character")
      expect_type(plot_data_function(), "closure")
    }
  )
})
