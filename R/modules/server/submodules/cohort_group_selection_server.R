cohort_group_selection_server <- function(
    input,
    output,
    session,
    filter_obj,
    selected_dataset
){
    ns <- session$ns

    available_tags <- shiny::reactive({
        iatlas.app::query_dataset_tags(selected_dataset()) %>%
            tibble::deframe(.)
    })

    # dataset_to_group_tbl <- dplyr::tribble(
    #     ~group,                ~dataset, ~type,
    #     "Immune Feature Bins", "TCGA",    "custom",
    #     "Driver Mutation",     "TCGA",    "custom",
    #     "Immune Feature Bins", "PCAWG",   "custom",
    # )

    #TODO: add non tag groups
    available_groups <- shiny::reactive({
        available_tags()
    })

    default_group <- shiny::reactive({
        shiny::req(available_groups())
        available_groups()[[1]]
    })

    output$select_group_ui <- shiny::renderUI({
        shiny::req(available_groups(), default_group())
        shiny::selectInput(
            inputId = ns("group_choice"),
            label = shiny::strong("Select or Search for Grouping Variable"),
            choices = available_groups(),
            selected = default_group()
        )
    })

    group_choice <- shiny::reactive({
        req(default_group())
        if (is.null(input$group_choice)) return(default_group())
        else return(input$group_choice)
    })


    # # This is so that the conditional panel can see the various shiny::reactives
    # output$display_driver_mutation <- shiny::reactive(
    #     # group_choice() == "Driver_Mutation"
    #     T
    # )
    #
    # shiny::outputOptions(
    #     output,
    #     "display_driver_mutation",
    #     suspendWhenHidden = FALSE
    # )
    #
    # # default_driver_mutation <- "ABL1:(NS)"
    # mutation_tbl <- shiny::reactive({
    #     iatlas.app::query_mutations(type = "driver_mutation") %>%
    #         dplyr::mutate(
    #             "mutation" = stringr::str_c(.data$hgnc, ":", .data$code)
    #         )
    # })
    #
    # output$select_driver_mutation_group_ui <- shiny::renderUI({
    #     shiny::req(input$group_choice == "Driver_Mutation", mutation_tbl())
    #     shiny::selectInput(
    #         inputId  = ns("driver_mutation_choice"),
    #         label    = "Select or Search for Driver Mutation",
    #         choices  = mutation_tbl() %>%
    #             dplyr::select("mutation", "id") %>%
    #             tibble::deframe(.)
    #         # selected = default_driver_mutation
    #     )
    # })
    #
    # driver_mutation <- shiny::reactive({
    #     if (is.null(input$driver_mutation_choice)) {
    #         return(default_driver_mutation)
    #     } else {
    #         return(input$driver_mutation_choice)
    #     }
    # })
    #
    # # This is so that the conditional panel can see the various shiny::reactives
    # output$display_immune_feature_bins <- shiny::reactive(group_choice() == "Immune_Feature_Bins")
    # shiny::outputOptions(output, "display_immune_feature_bins", suspendWhenHidden = FALSE)
    #
    # output$select_immune_feature_bins_group_ui <- shiny::renderUI({
    #     shiny::selectInput(
    #         inputId = ns("immune_feature_bin_choice"),
    #         label = "Select or Search for feature",
    #         choices = iatlas.app::create_feature_named_list(
    #             sample_ids = filter_obj()$sample_ids
    #         )
    #     )
    # })
    #

    cohort_obj <- shiny::reactive({
        shiny::req(
            group_choice(),
            filter_obj(),
            selected_dataset()
        )
        # if (group_choice() == "Driver_Mutation") {
        #     shiny::req(driver_mutation())
        # } else if (group_choice() == "Immune_Feature_Bins") {
        #     shiny::req(
        #         input$immune_feature_bin_choice,
        #         input$immune_feature_bin_number
        #     )
        # }
        iatlas.app::create_cohort_object(
            filter_obj(),
            selected_dataset(),
            group_choice()
        )
    })

    return(cohort_obj)
}
