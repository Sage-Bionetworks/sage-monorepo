test_that("immune_feature_correlations_server", {
  shiny::testServer(
    immune_feature_correlations_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(pcawg_immune_subtype_cohort_obj)
    ),
    {
      session$setInputs("response_choice" = "B_cells_naive")
      session$setInputs("class_choice" = "EPIC")
      session$setInputs("correlation_method" = "pearson")
      expect_type(output$class_selection_ui, "list")
      expect_type(output$response_selection_ui, "list")
      expect_type(output$heatmap, "character")
    }
  )
})
