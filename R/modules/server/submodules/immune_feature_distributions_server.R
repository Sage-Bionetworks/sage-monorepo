immune_feature_distributions_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl,
    group_name,
    feature_named_list,
    plot_colors
){

    ns <- session$ns

    source("R/immune_feature_distributions_functions.R", local = T)
    source("R/modules/server/submodules/distribution_plot_server.R", local = T)

    output$selection_ui <- shiny::renderUI({
        shiny::req(feature_named_list())
        shiny::selectInput(
            ns("feature_choice_id"),
            label = "Select or Search for Variable",
            selected = .GlobalEnv$get_feature_id_from_display(
                "Leukocyte Fraction"
            ),
            choices = feature_named_list()
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
            sample_tbl(),
            input$feature_choice_id,
            input$scale_method_choice
        )
        build_ifd_distplot_tbl(
            sample_tbl(),
            input$feature_choice_id,
            input$scale_method_choice
        )
    })

    shiny::callModule(
        distribution_plot_server,
        "immune_feature_dist_plot",
        distplot_tbl    = distplot_tbl,
        group_tbl       = group_tbl,
        distplot_type   = shiny::reactive(input$plot_type_choice),
        distplot_colors = plot_colors,
        distplot_xlab   = group_name,
        distplot_ylab   = feature_plot_label,
        distplot_title  = feature_name
    )
}
