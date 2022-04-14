test_that("clinical_outcomes_survival_server", {
  shiny::testServer(
    clinical_outcomes_survival_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      )
    ),
    {
      session$setInputs("time_feature_choice" = "OS_time")
      session$setInputs("class_choice" = "DNA Alteration")
      session$setInputs("risktable" = T)
      session$setInputs("confint" = F)
      expect_type(output$time_feature_selection_ui, "list")
      expect_type(output$survival_plot, "list")
    }
  )
})
