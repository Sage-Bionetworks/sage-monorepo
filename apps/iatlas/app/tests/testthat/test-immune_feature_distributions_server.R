test_that("module_works", {
  shiny::testServer(
    immune_feature_distributions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
      session$setInputs("feature_choice_name" = "leukocyte_fraction")
      session$setInputs("scale_method_choice" = "None")
      expect_type(output$selection_ui, "list")
      expect_type(feature_choice_display(), "character")
      expect_type(feature_plot_label(), "character")
    }
  )
})
