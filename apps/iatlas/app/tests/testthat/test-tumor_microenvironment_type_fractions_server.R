
test_that("tumor_microenvironment_type_fractions_server_pcawg", {
  shiny::testServer(
    tumor_microenvironment_type_fractions_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      ),
      "mock_event_data" = shiny::reactive(data.frame(
        "curveNumber" = 1,
        "pointNumber" = 2,
        "x" = "C4",
        "y" = 6.588,
        "key" = "C4"
      ))
    ),
    {
      session$setInputs(
        "barplot-feature_class_choice" =
          "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class"
      )

      expect_type(sample_data_function(), "closure")
      sample_data <- sample_data_function()("Immune Cell Proportion - Original")
      expect_true(tibble::is_tibble(sample_data))
      expect_named(
        sample_data,
        c(
          "sample_name",
          "group_name",
          "feature_name",
          "feature_display",
          "feature_value"
        )
      )

      scatterplot_data <- result()$scatterplot_data
      expect_true(tibble::is_tibble(scatterplot_data))
      expect_named(scatterplot_data, c("x", "y", "text"))
      barplot_data <- result()$barplot_data
      expect_true(tibble::is_tibble(barplot_data))
      expect_named(
        barplot_data,
        c('group_display', 'feature_display', 'text', 'MEAN', 'SE')
      )
    }
  )
})
