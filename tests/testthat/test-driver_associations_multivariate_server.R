test_that("multivariate_driver_server", {
  shiny::testServer(
    multivariate_driver_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
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
      expect_type(status_tbl(), "list")
      expect_named(
        status_tbl(),
        c(
          "sample",
          "mutation_id",
          "entrez",
          "hgnc",
          "mutation_code",
          "mutation_name",
          "mutation_display",
          "mutation_status"
        )
      )
      session$setInputs("group_mode" = "By group")
      print(combined_tbl())
    }
  )
})
