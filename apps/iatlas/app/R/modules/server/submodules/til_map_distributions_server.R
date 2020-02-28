til_map_distributions_server <- function(
    input,
    output,
    session,
    sample_tbl,
    cohort_obj
){

    ns <- session$ns

    source("R/modules/server/submodules/distribution_plot_server.R", local = T)
    source("R/til_map_distributions_functions.R", local = T)

    output$selection_ui <- shiny::renderUI({
        shiny::req(cohort_obj())
        shiny::selectInput(
            ns("feature_choice_id"),
            label = "Select or Search for Variable",
            choices = get_til_map_named_list(cohort_obj()$feature_tbl)
        )
    })

    feature_name <- shiny::reactive({
        shiny::req(input$feature_choice_id)
        input$feature_choice_id %>%
            as.integer() %>%
            .GlobalEnv$get_feature_display_from_id()
    })

    feature_plot_label <- shiny::reactive({
        shiny::req(
            feature_name(),
            input$scale_method_choice
        )
        .GlobalEnv$transform_feature_string(
            feature_name(),
            input$scale_method_choice
        )
    })

    distplot_tbl <- shiny::reactive({
        shiny::req(
            sample_tbl(),
            input$feature_choice_id,
            input$scale_method_choice
        )
        build_tilmap_distplot_tbl(
            sample_tbl(),
            input$feature_choice_id,
            input$scale_method_choice
        )
    })

    shiny::callModule(
        distribution_plot_server,
        "tilmap_dist_plot",
        distplot_tbl    = distplot_tbl,
        group_tbl       = shiny::reactive(cohort_obj()$group_tbl),
        distplot_type   = shiny::reactive(input$plot_type_choice),
        distplot_colors = shiny::reactive(cohort_obj()$plot_colors),
        distplot_xlab   = shiny::reactive(cohort_obj()$group_name),
        distplot_ylab   = feature_plot_label,
        distplot_title  = feature_name
    )
}
