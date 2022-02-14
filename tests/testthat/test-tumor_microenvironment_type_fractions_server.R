
test_that("tumor_microenvironment_type_fractions_server_pcawg", {
  shiny::testServer(
    tumor_microenvironment_type_fractions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      )
    ),
    {
      session$setInputs(
        "barplot-feature_class_choice" =
          "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class"
      )
      session$setInputs("barplot-mock_event_data" = data.frame(
        "curveNumber" = 1,
        "pointNumber" = 2,
        "x" = "C4",
        "y" = 6.588,
        "key" = "C4"
      ))
      expect_type(feature_classes(), "character")
      expect_type(plot_data_function(), "closure")
      expect_type(
        plot_data_function()("Immune Cell Proportion - Original"),
        "list"
      )

      scatterplot_data <- result$scatterplot_data()
      expect_type(scatterplot_data, "list")
      expect_named(scatterplot_data, c("x", "y", "text"))
      barplot_data <- result$barplot_data()
      expect_type(barplot_data, "list")
      expect_named(
        barplot_data,
        c('group_name', 'feature_display', 'text', 'MEAN', 'SE')
      )
    }
  )
})
