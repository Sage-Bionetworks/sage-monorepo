immune_feature_distributions_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    ns <- session$ns

    source("R/immune_feature_distributions_functions.R", local = T)
    source("R/modules/server/submodules/distribution_plot_server.R", local = T)

    output$selection_ui <- shiny::renderUI({
        shiny::req(cohort_obj())
        shiny::selectInput(
            ns("feature_choice_id"),
            label = "Select or Search for Variable",
            selected = .GlobalEnv$get_feature_id_from_display(
                "Leukocyte Fraction"
            ),
            choices = .GlobalEnv$create_nested_named_list(
                cohort_obj()$feature_tbl, values_col = "id"
            )
        )
    })

    feature_name <- shiny::reactive({
        shiny::req(input$feature_choice_id)
        input$feature_choice_id %>%
            as.integer() %>%
            .GlobalEnv$get_feature_display_from_id()
    })

    feature_plot_label <- shiny::reactive({
        shiny::req(input$scale_method_choice)
        .GlobalEnv$transform_feature_string(
            feature_name(),
            input$scale_method_choice
        )
    })

    distplot_tbl <- shiny::reactive({
        shiny::req(
            cohort_obj(),
            input$feature_choice_id,
            input$scale_method_choice
        )
        build_ifd_distplot_tbl(
            cohort_obj()$sample_tbl,
            input$feature_choice_id,
            input$scale_method_choice
        )
    })

    shiny::callModule(
        distribution_plot_server,
        "immune_feature_dist_plot",
        cohort_obj,
        distplot_tbl    = distplot_tbl,
        distplot_type   = shiny::reactive(input$plot_type_choice),
        distplot_ylab   = feature_plot_label,
        distplot_title  = feature_name
    )
}
