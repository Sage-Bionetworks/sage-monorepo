test_that("cellimage_main_server", {
  shiny::testServer(
    cellimage_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj
      )
    ),
    {
      expect_equal(cohort_groups(), c("C1", "C2", "C3", "C4", "C5", "C6"))
      expect_type(output$select_group1_ui , "list")
      expect_type(output$select_group2_ui , "list")
    }
  )
})
