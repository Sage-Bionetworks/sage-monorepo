test_that("til_map_distributions_server", {
  shiny::testServer(
    til_map_distributions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
      expect_type(output$selection_ui, "list")
      session$setInputs("feature_choice" = "til_percentage")
      session$setInputs("scale_method_choice" = "None")
      expect_equal(feature_choice_display(), "TIL Regional Fraction (Percent)")
      expect_equal(feature_plot_label(), "TIL Regional Fraction (Percent)")
    }
  )
})
