cohort_group_selection_server <- function(
    input,
    output,
    session,
    filter_obj,
    selected_dataset
){
    ns <- session$ns

    tag_group_tbl <- shiny::reactive(
        iatlas.app::query_dataset_tags(selected_dataset())
    )

    custom_group_tbl <- shiny::reactive({
        shiny::req(selected_dataset())
        iatlas.app::build_custom_group_tbl(selected_dataset())
    })

    available_groups_list <- shiny::reactive({
        shiny::req(tag_group_tbl(), custom_group_tbl())
        build_cohort_group_list(tag_group_tbl(), custom_group_tbl())
    })

    default_group <- shiny::reactive({
        shiny::req(available_groups_list())
        available_groups_list()[[1]]
    })

    output$select_group_ui <- shiny::renderUI({
        shiny::req(available_groups_list(), default_group())
        shiny::selectInput(
            inputId = ns("group_choice"),
            label = shiny::strong("Select or Search for Grouping Variable"),
            choices = available_groups_list(),
            selected = default_group()
        )
    })

    dedupe <- function(r) {
        shiny::makeReactiveBinding("val")
        shiny::observe(val <<- r(), priority = 10)
        shiny::reactive(val)
    }

    group_choice <- dedupe(shiny::reactive({
        req(default_group())
        if (is.null(input$group_choice)) return(default_group())
        else return(input$group_choice)
    }))


    # This is so that the conditional panel can see the various shiny::reactives
    output$display_driver_mutation <- shiny::reactive({
        shiny::req(group_choice())
        group_choice() == "Driver Mutation"
    })

    shiny::outputOptions(
        output,
        "display_driver_mutation",
        suspendWhenHidden = FALSE
    )

    mutation_tbl <- shiny::reactive(iatlas.app::build_cohort_mutation_tbl())

    output$select_driver_mutation_group_ui <- shiny::renderUI({
        shiny::req(group_choice() == "Driver Mutation", mutation_tbl())
        shiny::selectInput(
            inputId  = ns("driver_mutation_id_choice"),
            label    = "Select or Search for Driver Mutation",
            choices  = mutation_tbl() %>%
                dplyr::select("mutation", "id") %>%
                tibble::deframe(.)
        )
    })

    # This is so that the conditional panel can see the various shiny::reactives
    output$display_immune_feature_bins <- shiny::reactive({
        shiny::req(group_choice())
        group_choice() == "Immune Feature Bins"
    })

    shiny::outputOptions(
        output,
        "display_immune_feature_bins",
        suspendWhenHidden = FALSE
    )

    feature_bin_tbl <- shiny::reactive({
        shiny::req(group_choice() == "Immune Feature Bins", selected_dataset())
        selected_dataset() %>%
            iatlas.app::query_features_by_class() %>%
            dplyr::select("class", "display", "name")
    })

    # TODO: use sample names from feature object to query features, not dataset
    output$select_immune_feature_bins_group_ui <- shiny::renderUI({
        shiny::req(feature_bin_tbl())

        shiny::selectInput(
            inputId = ns("immune_feature_bin_choice"),
            label = "Select or Search for feature",
            choices = iatlas.app::create_nested_named_list(
                feature_bin_tbl(), values_col = "name"
            )
        )
    })

    cohort_obj <- shiny::reactive({
        shiny::req(
            group_choice(),
            filter_obj(),
            selected_dataset()
        )
        if (group_choice() == "Driver Mutation") {
            shiny::req(input$driver_mutation_id_choice, mutation_tbl())
            cohort_obj <- iatlas.app::build_cohort_object(
                filter_obj(),
                selected_dataset(),
                group_choice(),
                "custom",
                mutation_id = as.integer(input$driver_mutation_id_choice),
                mutation_tbl = mutation_tbl()
            )
        } else if (group_choice() == "Immune Feature Bins") {
            shiny::req(
                input$immune_feature_bin_choice,
                input$immune_feature_bin_number,
                feature_bin_tbl()
            )
            cohort_obj <- iatlas.app::build_cohort_object(
                filter_obj(),
                selected_dataset(),
                group_choice(),
                "custom",
                feature_name = input$immune_feature_bin_choice,
                bin_number = input$immune_feature_bin_number,
                feature_tbl = feature_bin_tbl()
            )
        } else {
            cohort_obj <- iatlas.app::build_cohort_object(
                filter_obj(),
                selected_dataset(),
                group_choice(),
                "tag"
            )
        }
        return(cohort_obj)
    })

    return(cohort_obj)
}
