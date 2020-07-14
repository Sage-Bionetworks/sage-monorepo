cohort_group_selection_server <- function(
    input,
    output,
    session,
    filter_obj,
    selected_dataset
){
    ns <- session$ns

    dataset_to_group_tbl <- dplyr::tribble(
        ~group,                ~dataset, ~type, ~group_internal,
        "Immune Subtype",      "TCGA",   "tag", "Immune_Subtype",
        "TCGA Subtype",        "TCGA",   "tag", "TCGA_Subtype",
        "TCGA Study",          "TCGA",   "tag", "TCGA_Study",
        # "Gender",          "TCGA",   "sample",
        # "Race",            "TCGA",   "sample",
        # "Ethnicity",       "TCGA",   "sample",
        # "Immune Feature Bins", "TCGA",    NA,    "Immune_Feature_Bins",
        # "Driver Mutation",     "TCGA",    NA,    "Driver_Mutation",
        "Immune Subtype",      "PCAWG",   "tag", "Immune_Subtype",
        "PCAWG Study",         "PCAWG",   "tag", "PCAWG_Study"
        # "Immune Feature Bins", "PCAWG",   NA,    "Immune_Feature_Bins"
        # "Gender",          "PCAWG",  "sample",
        # "Race",            "PCAWG",  "sample"
    )

    available_groups <- shiny::reactive({
        shiny::req(selected_dataset())
        iatlas.app::get_cohort_available_groups(
            dataset_to_group_tbl, selected_dataset()
        )
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

    # This is so that the conditional panel can see the various shiny::reactives
    output$display_driver_mutation <- shiny::reactive(
        group_choice() == "Driver_Mutation"
    )

    shiny::outputOptions(
        output,
        "display_driver_mutation",
        suspendWhenHidden = FALSE
    )

    default_driver_mutation <- "ABL1:(NS)"
    dm_tbl <- shiny::reactive(build_dm_tbl())

    output$select_driver_mutation_group_ui <- shiny::renderUI({
        shiny::req(input$group_choice == "Driver_Mutation", dm_tbl())
        shiny::selectInput(
            inputId  = ns("driver_mutation_choice"),
            label    = "Select or Search for Driver Mutation",
            choices  = dm_tbl()$mutation,
            selected = default_driver_mutation
        )
    })

    driver_mutation <- shiny::reactive({
        if (is.null(input$driver_mutation_choice)) {
            return(default_driver_mutation)
        } else {
            return(input$driver_mutation_choice)
        }
    })

    # This is so that the conditional panel can see the various shiny::reactives
    output$display_immune_feature_bins <- shiny::reactive(group_choice() == "Immune_Feature_Bins")
    shiny::outputOptions(output, "display_immune_feature_bins", suspendWhenHidden = FALSE)

    output$select_immune_feature_bins_group_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId = ns("immune_feature_bin_choice"),
            label = "Select or Search for feature",
            choices = iatlas.app::create_feature_named_list(
                sample_ids = filter_obj()$sample_ids
            )
        )
    })

    cohort_obj <- shiny::reactive({
        shiny::req(
            group_choice(),
            filter_obj(),
            selected_dataset()
        )
        if (group_choice() == "Driver_Mutation") {
            shiny::req(driver_mutation())
        } else if (group_choice() == "Immune_Feature_Bins") {
            shiny::req(
                input$immune_feature_bin_choice,
                input$immune_feature_bin_number
            )
        }
        iatlas.app::create_cohort_object(
            filter_obj(),
            selected_dataset(),
            group_choice(),
            driver_mutation(),
            as.integer(input$immune_feature_bin_choice),
            as.integer(input$immune_feature_bin_number)
        )
    })

    return(cohort_obj)
}
