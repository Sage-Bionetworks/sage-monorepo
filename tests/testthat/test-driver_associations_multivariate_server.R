# TODO: fix slow query, uncomment tests
test_that("multivariate_driver_server", {
  shiny::testServer(
    multivariate_driver_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(get_tcga_immune_subtype_cohort_obj_50())
    ),
    {
      expect_type(output$response_options, "list")
      expect_type(numerical_covariate_tbl(), "list")
      expect_named(
        numerical_covariate_tbl(), c("class", "display", "feature")
      )
      expect_type(categorical_covariate_tbl(), "list")
      expect_named(
        categorical_covariate_tbl(), c("class", "display", "feature")
      )
      session$setInputs("response_choice" = "leukocyte_fraction")
      expect_equal(response_variable_display(), "Leukocyte Fraction")
      expect_equal(
        model_string_prefix(), "Leukocyte Fraction ~ Mutation status"
      )
      expect_null(covariates_obj()$categorical_covariates)
      expect_null(covariates_obj()$numerical_covariates)
      expect_equal(
        covariates_obj()$display_string, "Leukocyte Fraction ~ Mutation status"
      )
      expect_equal(
        covariates_obj()$formula_string, "response ~ status"
      )
      expect_equal(output$model_text, "Leukocyte Fraction ~ Mutation status")
      expect_null(covariate_tbl())
      expect_type(response_tbl(), "list")
      expect_named(response_tbl(), c("sample", "response"))
      expect_true(nrow(response_tbl()) > 0)
      # expect_type(status_tbl(), "list")
      # expect_named(
      #   status_tbl(),
      #   c(
      #     "mutation",
      #     "sample",
      #     "status"
      #   )
      # )
      # expect_true(nrow(status_tbl()) > 0)
      # session$setInputs("group_mode" = "Across groups")
      # expect_type(combined_tbl(), "list")
      # expect_named(combined_tbl(), c("response", "status", "label"))
      # session$setInputs("group_mode" = "By group")
      # expect_type(combined_tbl(), "list")
      # expect_named(combined_tbl(), c("response", "status", "label"))
      # session$setInputs("min_mutants" = 2)
      # session$setInputs("min_wildtype" = 2)
      # expect_type(labels(), "character")
      # expect_true(length(labels()) > 0)
      # expect_type(filtered_tbl(), "list")
      # expect_named(filtered_tbl(), c("response", "status", "label"))
      # expect_true(nrow(filtered_tbl()) > 0)
      # expect_type(pvalue_tbl(), "list")
      # expect_named(pvalue_tbl(), c('label', 'p_value', 'log10_p_value'))
      # expect_type(effect_size_tbl(), "list")
      # expect_named(
      #   effect_size_tbl(),
      #   c('label', 'fold_change', 'log10_fold_change')
      # )
      # session$setInputs("calculate_button" = 1)
      # expect_type(volcano_plot_tbl(), "list")
      # expect_named(
      #   volcano_plot_tbl(),
      #   c('label', 'p_value', 'log10_p_value', 'fold_change', 'log10_fold_change')
      # )
      # expect_error(selected_volcano_result())
      # expect_error(violin_plot_tbl())
      # expect_error(output$violin_plot)
    }
  )
})
