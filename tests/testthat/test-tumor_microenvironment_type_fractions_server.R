test_that("module_works", {
  shiny::testServer(
    tumor_microenvironment_type_fractions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj)
    ),
    {
      session$setInputs("fraction_group_choice" = "Immune Cell Proportion - Differentiated Lymphoid and Myeloid Cell Derivative Class")
      expect_type(output$barplot, "character")
    }
  )
})
