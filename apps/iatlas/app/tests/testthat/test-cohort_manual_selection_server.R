
test_that("cohort_manual_selection_server_pcawg", {
  shiny::testServer(
    cohort_manual_selection_server,
    {
      expect_type(default_dataset, "character")
      session$setInputs("dataset_selection-dataset_choice" = "PCAWG")
      expect_equal(selected_dataset(), "PCAWG")
      expect_equal(dataset(), "PCAWG")
      expect_named(filter_object(), c("samples", "filters"))
      expect_type(filter_object()$samples, "character")
      expect_equal(group_object()$dataset, "PCAWG")
      expect_equal(group_object()$group_name, "Immune_Subtype")
      expect_equal(group_object()$group_display, "Immune Subtype")
      expect_equal(group_object()$group_type, "tag")
      cohort_object <- session$getReturned()()
      expect_type(cohort_object, "list")
    }
  )
})
