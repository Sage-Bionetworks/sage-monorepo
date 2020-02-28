cohort_filter_selection_server <- function(
    input,
    output,
    session,
    selected_dataset,
    sample_ids
){

    source(
        "R/modules/server/submodules/insert_remove_element_server.R",
        local = T
    )
    source("R/modules/ui/submodules/elements_ui.R", local = T)
    source("R/modules/server/submodules/elements_server.R", local = T)
    source("R/cohort_filter_selection_functions.R", local = T)

    dataset_to_group_tbl <- dplyr::tribble(
        ~group,                 ~dataset, ~type,
        "Immune Subtype",       "TCGA",   "tag",
        "TCGA Subtype",         "TCGA",   "tag",
        "TCGA Study",           "TCGA",   "tag",
        # "Gender",          "TCGA",   "sample",
        # "Race",            "TCGA",   "sample",
        # "Ethnicity",       "TCGA",   "sample",
        # "Immune Subtype",  "PCAWG",  "tag",
        # "PCAWG Study",     "PCAWG",  "tag",
        # "Gender",          "PCAWG",  "sample",
        # "Race",            "PCAWG",  "sample"
    )

    # group filters -----------------------------------------------------------
    group_named_list <- shiny::reactive({
        create_cohort_group_named_list(dataset_to_group_tbl)
    })

    group_element_module_server <- shiny::reactive({
        shiny::req(group_named_list())
        purrr::partial(
            group_filter_element_server,
            group_named_list = group_named_list
        )
    })

    group_element_module_ui <- shiny::reactive(group_filter_element_ui)

    group_filter_output <- shiny::callModule(
        insert_remove_element_server,
        "group_filter",
        element_module = group_element_module_server,
        element_module_ui = group_element_module_ui,
        remove_ui_event = shiny::reactive(selected_dataset())
    )

    valid_group_filter_obj <- shiny::reactive({
        shiny::req(group_filter_output())
        group_filter_output() %>%
            shiny::reactiveValuesToList(.) %>%
            get_valid_group_filters()
    })

    group_filter_samples <- shiny::reactive({
        shiny::req(sample_ids())
        get_filtered_group_sample_ids(
            valid_group_filter_obj(),
            sample_ids()
        )
    })

    # numeric_filters ---------------------------------------------------------
    numeric_element_module_server <- shiny::reactive({
        purrr::partial(
            numeric_filter_element_server,
            feature_named_list = .GlobalEnv$create_feature_named_list
        )
    })

    numeric_element_module_ui <- shiny::reactive(numeric_filter_element_ui)

    numeric_filter_output <- shiny::callModule(
        insert_remove_element_server,
        "numeric_filter",
        element_module = numeric_element_module_server,
        element_module_ui = numeric_element_module_ui,
        remove_ui_event = shiny::reactive(selected_dataset())
    )

    valid_numeric_filter_obj <- shiny::reactive({
        shiny::req(numeric_filter_output())
        numeric_filter_output() %>%
            shiny::reactiveValuesToList(.) %>%
            get_valid_numeric_filters()
    })

    numeric_filter_samples <- shiny::reactive({
        shiny::req(sample_ids())
        get_filtered_feature_sample_ids(
            valid_numeric_filter_obj(),
            sample_ids()
        )
    })

    selected_samples <- shiny::reactive({
        shiny::req(numeric_filter_samples(), group_filter_samples())
        intersect(numeric_filter_samples(), group_filter_samples())
    })

    output$samples_text <- shiny::renderText({
        c("Number of current samples:", length(selected_samples()))
    })

    filter_obj <- shiny::reactive({
        create_cohort_filter_object(
            selected_samples(),
            valid_numeric_filter_obj(),
            valid_group_filter_obj()
        )
    })

    return(filter_obj)
}
