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
    source("R/modules/server/submodules/plotly_server.R", local = T)

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
        shiny::req(input$scale_method)
        .GlobalEnv$transform_feature_string(
            feature_name(),
            input$scale_method
        )
    })

    # distplot ----------------------------------------------------------------
    distplot_tbl <- shiny::reactive({
        shiny::req(
            sample_tbl(),
            input$feature_choice_id,
            input$scale_method
        )
        build_distplot_tbl(
            sample_tbl(),
            input$feature_choice_id,
            input$scale_method
        )
    })

    distplot_function <- shiny::reactive({
        shiny::req(input$plot_type)
        switch(
            input$plot_type,
            "Violin" = create_violinplot,
            "Box" = create_boxplot
        )
    })

    output$distplot <- plotly::renderPlotly({
        shiny::req(
            distplot_tbl(),
            group_name(),
            feature_name(),
            plot_colors()
        )

        distplot_function()(
            distplot_tbl(),
            source_name = "immune_feature_dist_plot",
            xlab = group_name(),
            ylab = feature_plot_label(),
            title = feature_name(),
            fill_colors = plot_colors()
        )
    })

    distplot_eventdata <- shiny::reactive({
        plotly::event_data("plotly_click", "immune_feature_dist_plot")
    })

    shiny::callModule(
        plotly_server,
        "immune_feature_dist_plot",
        plot_tbl       = distplot_tbl,
        plot_eventdata = distplot_eventdata,
        group_tbl      = group_tbl,
    )


    # histplot ----------------------------------------------------------------
    histplot_tbl <- shiny::reactive({
        shiny::req(distplot_tbl())
        shiny::validate(shiny::need(distplot_eventdata(), "Click above plot"))
        selected_group <- get_selected_values_from_eventdata(
            distplot_eventdata()
        )

        groups <- dplyr::pull(distplot_tbl(), "x")
        shiny::validate(shiny::need(
            selected_group %in% groups, "Click above barchart"
        ))

        build_histplot_tbl(distplot_tbl(), selected_group)
    })

    output$histplot <- plotly::renderPlotly({
        shiny::req(
            histplot_tbl(),
            feature_plot_label(),
            distplot_eventdata()$x[[1]]
        )

        .GlobalEnv$create_histogram(
            histplot_tbl(),
            source_name = "immune_feature_hist_plot",
            x_lab = feature_plot_label(),
            y_lab = "Count",
            title = distplot_eventdata()$x[[1]],
        )
    })

    shiny::callModule(
        plotly_server,
        "immune_feature_hist_plot",
        plot_tbl = histplot_tbl
    )
}
