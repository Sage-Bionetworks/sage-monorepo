test_that("extracellular_network_server", {
  shiny::testServer(
    extracellular_network_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
      expect_true(T)
    }
  )
})
