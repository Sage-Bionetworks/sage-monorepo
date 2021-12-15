test_that("extracellular_network_server_tcga", {
  shiny::testServer(
    extracellular_network_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj
      )
    ),
    {
      expect_true(shiny::is.reactive(show_submodule))
      expect_type(show_submodule(), "closure")
      expect_true(show_submodule()(cohort_obj()))
    }
  )
})

test_that("extracellular_network_server_pcawg", {
  shiny::testServer(
    extracellular_network_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      )
    ),
    {
      expect_true(shiny::is.reactive(show_submodule))
      expect_type(show_submodule(), "closure")
      expect_true(show_submodule()(cohort_obj()))
    }
  )
})
