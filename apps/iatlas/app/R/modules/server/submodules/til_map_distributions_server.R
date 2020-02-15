til_map_distributions_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl,
    group_name,
    plot_colors
){

    ns <- session$ns

    source("R/modules/server/submodules/distribution_plot_server.R", local = T)
    source("R/functions/til_map_distributions_functions.R", local = T)

    output$selection_ui <- shiny::renderUI({
        shiny::selectInput(
            ns("feature_choice_id"),
            label = "Select or Search for Variable",
            selected = get_leukocyte_fraction_id(),
            choices = get_til_map_named_list()
        )
    })

    feature_name <- shiny::reactive({
        print(input$feature_choice_id)
        shiny::req(input$feature_choice_id)
        get_feature_name(as.integer(input$feature_choice_id))
    })

    feature_plot_label <- shiny::reactive({
        print(input$scale_method_choice)
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
        build_distplot_tbl(
            sample_tbl(),
            input$feature_choice_id,
            input$scale_method_choice
        )
    })

    shiny::callModule(
        distribution_plot_server,
        "tilmap_dist_plot",
        distplot_tbl    = distplot_tbl,
        group_tbl       = group_tbl,
        distplot_type   = shiny::reactive(input$plot_type_choice),
        distplot_colors = plot_colors,
        distplot_xlab   = group_name,
        distplot_ylab   = feature_plot_label,
        distplot_title  = feature_name
    )
}
