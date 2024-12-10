model_selection_server <- function(
  id,
  numerical_covariate_tbl,
  categorical_covariate_tbl,
  model_string_prefix,
  model_formula_prefix = shiny::reactive("response ~ status")
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      # numeric covariate ui --------------------------------------------------

      numerical_covariate_list <- shiny::reactive({
        iatlas.modules::create_nested_named_list(
          numerical_covariate_tbl(),
          names_col1 = "class",
          names_col2 = "display",
          values_col = "feature"
        )
      })

      numeric_covariate_module <- shiny::reactive({
        purrr::partial(
          iatlas.modules2::numeric_model_covariate_element_server,
          covariate_list = numerical_covariate_list
        )
      })

      numeric_covariate_module_ui <- shiny::reactive(
        iatlas.modules2::numeric_model_covariate_element_ui
      )

      numeric_covariate_output <- iatlas.modules2::insert_remove_element_server(
        "select_numeric_covariate",
        element_module = numeric_covariate_module,
        element_module_ui = numeric_covariate_module_ui
      )

      numerical_covariates <- shiny::reactive({
        numeric_covariate_output() %>%
          shiny::reactiveValuesToList(.) %>%
          get_items_from_numeric_covariate_output(
            .,
            "covariate_choice_name"
          )
      })

      numerical_transformations <- shiny::reactive({
        numeric_covariate_output() %>%
          shiny::reactiveValuesToList(.) %>%
          get_items_from_numeric_covariate_output(
            .,
            "transformation_choice"
          )
      })

      numerical_display_string <- shiny::reactive({
        if(is.null(numerical_covariates())) return(NULL)
        if(is.null(numerical_transformations())) return(NULL)
        covs <- numerical_covariate_tbl() %>%
          dplyr::filter(.data$feature %in% numerical_covariates()) %>%
          dplyr::pull("display") %>%
          stringr::str_c(collapse = " + ")

        create_numerical_covariate_string(
          covs,
          numerical_transformations(),
          iatlas.modules::transform_feature_string
        )
      })

      numerical_formula_string <- shiny::reactive({
        if(is.null(numerical_covariates())) return(NULL)
        if(is.null(numerical_transformations())) return(NULL)

        create_numerical_covariate_string(
          numerical_covariates(),
          numerical_transformations(),
          iatlas.modules::transform_feature_formula
        )
      })

      # categorical covariate ui ----------------------------------------------

      categorical_covariate_list <- shiny::reactive({
        iatlas.modules::create_nested_named_list(
          categorical_covariate_tbl(),
          names_col1 = "class",
          names_col2 = "display",
          values_col = "feature"
        )
      })

      categorical_covariate_module <- shiny::reactive({
        purrr::partial(
          iatlas.modules2::categorical_model_covariate_element_server,
          covariate_list = categorical_covariate_list
        )
      })

      categorical_covariate_module_ui <- shiny::reactive(
        iatlas.modules2::categorical_model_covariate_element_ui
      )

      categorical_covariate_output <- iatlas.modules2::insert_remove_element_server(
        "select_categorical_covariate",
        element_module = categorical_covariate_module,
        element_module_ui = categorical_covariate_module_ui
      )

      categorical_covariates <- shiny::reactive({
        categorical_covariate_output() %>%
          shiny::reactiveValuesToList(.) %>%
          get_names_from_categorical_covariate_output()
      })

      categorical_display_string <- shiny::reactive({
        if(is.null(categorical_covariates())) return(NULL)
        categorical_covariate_tbl() %>%
          dplyr::filter(.data$feature %in% categorical_covariates()) %>%
          dplyr::pull("display") %>%
          stringr::str_c(collapse = " + ")
      })

      categorical_formula_string <- shiny::reactive({
        if(is.null(categorical_covariates())) return(NULL)
        stringr::str_c(categorical_covariates(), collapse = " + ")
      })

      # combine covariataes into output ---------------------------------------

      display_string <- shiny::reactive({
        req(model_string_prefix())
        create_covariate_string(
          model_string_prefix(),
          numerical_display_string(),
          categorical_display_string()
        )
      })

      formula_string <- shiny::reactive({
        req(model_formula_prefix)
        create_covariate_string(
          model_formula_prefix(),
          numerical_formula_string(),
          categorical_formula_string()
        )
      })

      shiny::reactive({
        list(
          "categorical_covariates"    = categorical_covariates(),
          "numerical_covariates"      = numerical_covariates(),
          "numerical_transformations" = numerical_transformations(),
          "display_string"            = display_string(),
          "formula_string"            = formula_string()
        )
      })
    }
  )
}
