test_that("univariate_driver_server", {
  shiny::testServer(
    univariate_driver_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
      expect_type(response_option_list(), "list")
      expect_type(output$response_option_ui, "list")
      session$setInputs("response_choice" = "leukocyte_fraction")
      expect_equal(response_variable_display(), "Leukocyte Fraction")
      session$setInputs("min_wt" = 30)
      session$setInputs("min_mut" = 30)
      expect_type(tags(), "character")
      expect_type(volcano_plot_tbl(), "list")
      expect_named(
        volcano_plot_tbl(),
        c(
          "label",
          "p_value",
          "log10_p_value",
          "log10_fold_change",
          "group",
          "entrez",
          "mutation_code"
        )
      )
      expect_type(output$result_text, "character")
      expect_type(output$volcano_plot, "character")
      expect_error(selected_volcano_result())
      expect_error(violin_plot_tbl())
      expect_error(output$violin_plot)
    }
  )
})
