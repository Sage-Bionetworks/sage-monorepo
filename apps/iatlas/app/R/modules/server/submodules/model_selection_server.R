model_selection_server <- function(
    input,
    output,
    session,
    numerical_covariate_tbl,
    categorical_covariate_tbl,
    model_string_prefix,
    model_formula_prefix = "response ~ status"
){

    source(
        "R/modules/server/submodules/insert_remove_element_server.R",
        local = T
    )

    source("R/model_selection_functions.R")

    # numeric covariate ui -----------------------------------------------------
    numeric_covariate_module <- shiny::reactive({
        purrr::partial(
            numeric_model_covariate_element_server,
            covariate_tbl = numerical_covariate_tbl
        )
    })

    numeric_covariate_module_ui <- shiny::reactive(
        numeric_model_covariate_element_ui
    )

    numeric_covariate_output <- shiny::callModule(
        insert_remove_element_server,
        "select_numeric_covariate",
        element_module = numeric_covariate_module,
        element_module_ui = numeric_covariate_module_ui
    )

    numerical_covariates <- shiny::reactive({
        numeric_covariate_output() %>%
            shiny::reactiveValuesToList(.) %>%
            get_items_from_numeric_covariate_output("covariate_choice_id")
    })

    numerical_transformations <- shiny::reactive({
        numeric_covariate_output() %>%
            shiny::reactiveValuesToList(.) %>%
            get_items_from_numeric_covariate_output("transformation_choice")
    })

    numerical_display_string <- shiny::reactive({
        create_numerical_covariate_string(
            numerical_covariates(),
            numerical_transformations()
        )
    })

    numerical_formula_string <- shiny::reactive({
        if (!is.null(numerical_covariates())) {
            string <- numerical_covariates() %>%
                purrr::map(~translate_value(
                    numerical_covariate_tbl(),
                    .x,
                    "feature",
                    "internal")
                ) %>%
                purrr::map2_chr(
                    numerical_transformations(),
                    transform_feature_formula
                ) %>%
                stringr::str_c(collapse = " + ")
            return(string)
        } else{
            return(NULL)
        }
    })

    # categorical covariate ui -------------------------------------------------
    categorical_covariate_module <- shiny::reactive({
        purrr::partial(
            categorical_model_covariate_element_server,
            covariate_tbl = categorical_covariate_tbl
        )
    })

    categorical_covariate_module_ui <- shiny::reactive(
        categorical_model_covariate_element_ui
    )

    categorical_covariate_output <- shiny::callModule(
        insert_remove_element_server,
        "select_categorical_covariate",
        element_module = categorical_covariate_module,
        element_module_ui = categorical_covariate_module_ui
    )

    categorical_covariates <- shiny::reactive({
        categorical_covariate_output() %>%
            shiny::reactiveValuesToList(.) %>%
            get_ids_from_categorical_covariate_output()
    })

    categorical_display_string <- shiny::reactive({
        create_categorical_covariate_string(categorical_covariates())
    })

    categorical_formula_string <- shiny::reactive({
        if (!is.null(categorical_covariates())) {
            string <- categorical_covariates() %>%
                purrr::map(~translate_value(
                    categorical_covariate_tbl(),
                    .x,
                    "feature",
                    "internal")
                ) %>%
                stringr::str_c(collapse = " + ")
            return(string)
        } else{
            return(NULL)
        }
    })

    # combine covariataes into output ------------------------------------------

    display_string <- shiny::reactive({


        req(model_string_prefix())

        string <- model_string_prefix()
        if (!is.null(categorical_display_string())) {
            string <- paste0(string, " + ",  categorical_display_string())
        }
        if (!is.null(numerical_display_string())) {
            string <- paste0(string, " + ",  numerical_display_string())
        }
        print(string)
        return(string)
    })

    formula_string <- shiny::reactive({
        req(model_formula_prefix)

        string <- model_formula_prefix
        if (!is.null(categorical_formula_string())) {
            string <- paste0(string, " + ",  categorical_formula_string())
        }
        if (!is.null(numerical_formula_string())) {
            string <- paste0(string, " + ",  numerical_formula_string())
        }
        return(string)
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
