numerical_covariate_tbl <- iatlasGraphQLClient::query_features() %>%
  dplyr::select("class", "display", "feature" = "name") %>%
  shiny::reactiveVal()

categorical_covariate_tbl <-
  dplyr::tribble(
    ~class,     ~display,         ~feature,
    "Groups",   "Immune Subtype", "Immune_Subtype",
    "Groups",   "TCGA Study",     "TCGA_Study",
    "Groups",   "TCGA Subtype",   "TCGA_Subtype"
  ) %>%
  shiny::reactiveVal()

test_that("model_selection_server", {
  shiny::testServer(
    model_selection_server,
    args = list(
      "numerical_covariate_tbl" = numerical_covariate_tbl,
      "categorical_covariate_tbl" = categorical_covariate_tbl,
      "model_string_prefix" = shiny::reactiveVal("Feature ~ Status")
    ),
    {
      expect_type(numerical_covariate_list(), "list")
      expect_type(numeric_covariate_module(), "closure")
      expect_null(numerical_covariates())
      expect_null(numerical_transformations())
      expect_null(numerical_display_string())
      expect_null(numerical_formula_string())

      expect_type(categorical_covariate_list(), "list")
      expect_type(categorical_covariate_module(), "closure")
      expect_null(categorical_covariates())
      expect_null(categorical_display_string())
      expect_null(categorical_formula_string())

      expect_equal(display_string(), "Feature ~ Status")
      expect_equal(formula_string(), "response ~ status")

      result <- group_object <- session$getReturned()()
      expect_type(result, "list")
    }
  )
})
