test_that("tumor_microenvironment_server", {
  shiny::testServer(
    tumor_microenvironment_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      )
    ),
    {
      expect_true(shiny::is.reactive(show_ocp_submodule))
      expect_type(show_ocp_submodule(), "closure")
      expect_true(show_ocp_submodule()(cohort_obj()))

      expect_true(shiny::is.reactive(show_ctf_submodule))
      expect_type(show_ctf_submodule(), "closure")
      expect_true(show_ctf_submodule()(cohort_obj()))
    }
  )
})
