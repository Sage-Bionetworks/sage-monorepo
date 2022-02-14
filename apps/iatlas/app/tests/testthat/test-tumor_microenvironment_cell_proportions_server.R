test_that("tumor_microenvironment_cell_proportions_server", {
  shiny::testServer(
    tumor_microenvironment_cell_proportions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      )
    ),
    {
      session$setInputs("barplot-mock_event_data" = data.frame(
        "curveNumber" = 1,
        "pointNumber" = 2,
        "x" = "C4",
        "y" = 6.588,
        "key" = "C4"
      ))
      expect_type(plot_data_function(), "closure")
      plot_data <- plot_data_function()(NULL)
      expect_type(plot_data, "list")
      expect_named(
        plot_data,
        c(
          'sample_name',
          'group_name',
          'feature_name',
          'feature_display',
          'feature_value',
          'group_description'
        )
      )
      result <- session$getReturned()
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
