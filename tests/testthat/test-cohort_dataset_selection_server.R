test_that("cohort_dataset_selection_server", {
  shiny::testServer(
    cohort_dataset_selection_server,
    args = list("default_dataset" = "TCGA"),
    {
      expect_type(output$dataset_selection_ui, "list")
      session$setInputs("dataset_choice" = "PCAWG")
      expect_type(output$module_availibility_string, "character")
      expect_equal(session$getReturned()(), "PCAWG")
      session$setInputs("dataset_choice" = "TCGA")
      expect_equal(session$getReturned()(), "TCGA")
    }
  )
})
