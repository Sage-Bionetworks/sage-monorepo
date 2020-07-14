cohort_filter_selection_server <- function(
    input,
    output,
    session,
    selected_dataset,
    samples
){
    source(
        "R/modules/server/submodules/insert_remove_element_server.R",
        local = T
    )
    source("R/modules/ui/submodules/elements_ui.R", local = T)
    source("R/modules/server/submodules/elements_server.R", local = T)
<<<<<<< HEAD

    # tag filters -----------------------------------------------------------
    tag_named_list <- shiny::reactive({
        iatlas.app::create_cohort_tag_named_list(
            selected_dataset()
        )
=======
    source("R/cohort_filter_selection_functions.R", local = T)

    dataset_to_group_tbl <- dplyr::tribble(
        ~group,                 ~dataset, ~type,
        "Immune Subtype",       "TCGA",   "tag",
        "TCGA Subtype",         "TCGA",   "tag",
        "TCGA Study",           "TCGA",   "tag",
        # "Gender",          "TCGA",   "sample",
        # "Race",            "TCGA",   "sample",
        # "Ethnicity",       "TCGA",   "sample",
        "Immune Subtype",  "PCAWG",  "tag",
        "PCAWG Study",     "PCAWG",  "tag",
        # "Gender",          "PCAWG",  "sample",
        # "Race",            "PCAWG",  "sample"
    )

    # group filters -----------------------------------------------------------
    group_named_list <- shiny::reactive({
        create_cohort_group_named_list(dataset_to_group_tbl, selected_dataset())
>>>>>>> staging_cohort_selection_ux
    })

    tag_element_module_server <- shiny::reactive({
        shiny::req(tag_named_list())
        purrr::partial(
            tag_filter_element_server,
            tag_named_list = tag_named_list
        )
    })

    tag_element_module_ui <- shiny::reactive(tag_filter_element_ui)

    tag_filter_output <- shiny::callModule(
        insert_remove_element_server,
        "tags_filter",
        element_module = tag_element_module_server,
        element_module_ui = tag_element_module_ui,
        remove_ui_event = shiny::reactive(selected_dataset())
    )

    valid_tag_filter_obj <- shiny::reactive({
        shiny::req(tag_filter_output())
        tag_filter_output() %>%
            shiny::reactiveValuesToList(.) %>%
            get_valid_tag_filters()
    })

    tag_filter_samples <- shiny::reactive({
        shiny::req(samples)
        get_filtered_tag_samples(
            valid_tag_filter_obj(),
            samples()
        )
    })

    # # numeric_filters -------------------------------------------------------

    # numeric_filter_function <- shiny::reactive({
    #     shiny::req(selected_dataset())
    #     x <- function() {
    #         selected_dataset() %>%
    #             iatlas.app::query_features_by_class() %>%
    #             dplyr::select("class", "display", "feature" = "name") %>%
    #             create_nested_named_list()
    #     }
    #     print(x)
    #     return(x)
    # })

    numeric_element_module_server <- shiny::reactive({
        # x <- purrr::partial(
        #     iatlas.app::create_feature_named_list,
        #     sample_ids = samples()
        # )
        x <- function() {
            selected_dataset() %>%
                iatlas.app::query_features_by_class() %>%
                dplyr::select("class", "display", "feature" = "name") %>%
                create_nested_named_list()
        }

        print(x)

        purrr::partial(
            numeric_filter_element_server,
            feature_named_list = x
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
        # shiny::req(numeric_filter_samples(), tag_filter_samples())
        # intersect(numeric_filter_samples(), tag_filter_samples())
        shiny::req(numeric_filter_samples())
        numeric_filter_samples()
    })

    # output$samples_text <- shiny::renderText({
    #     c("Number of current samples:", length(selected_samples()))
    # })

    filter_obj <- shiny::reactive({
        list(
            "samples" = samples()
            # valid_numeric_filter_obj(),
            # valid_tag_filter_obj()
        )
    })

    return(filter_obj)
}
