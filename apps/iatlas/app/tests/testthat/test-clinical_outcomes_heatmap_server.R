test_that("clinical_outcomes_heatmap_server", {
  shiny::testServer(
    clinical_outcomes_heatmap_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      )
    ),
    {
      session$setInputs("time_feature_choice" = "OS_time")
      session$setInputs("class_choice" = "DNA Alteration")
      expect_type(output$class_selection_ui, "list")
      expect_type(output$time_feature_selection_ui, "list")
      expect_type(output$heatmap, "character")
    }
  )
})
