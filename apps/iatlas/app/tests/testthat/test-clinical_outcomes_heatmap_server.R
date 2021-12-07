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
      session$setInputs("mock_event_data" = data.frame(
        "x" = "C1", "y" = "Silent Mutation Rate"
      ))

      expect_type(output$class_selection_ui, "list")
      expect_type(output$time_feature_selection_ui, "list")

      expect_type(heatmap_tbl(), "list")
      expect_named(
        heatmap_tbl(),
        c(
          'sample',
          'group',
          'time',
          'status',
          'feature_display',
          'feature_value',
          'feature_order'
        )
      )

      expect_type(output$heatmap, "character")
      expect_type(heatmap_eventdata(), "list")
      expect_named(heatmap_eventdata(), c("x", "y"))
      expect_type(group_data(), "list")
      expect_named(group_data(), c("group", "description"))
    }
  )
})
