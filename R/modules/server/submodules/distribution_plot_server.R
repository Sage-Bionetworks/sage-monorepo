distribution_plot_server <- function(
    input,
    output,
    session,
    distplot_tbl,
    group_tbl,
    distplot_type,
    distplot_colors,
    distplot_xlab,
    distplot_ylab,
    distplot_title
){

    source("R/modules/server/submodules/plotly_server.R", local = T)

    distplot_function <- shiny::reactive({
        switch(
            distplot_type(),
            "Violin" = create_violinplot,
            "Box" = create_boxplot
        )
    })

    output$distplot <- plotly::renderPlotly({
        shiny::req(distplot_tbl(), distplot_function())

        distplot_function()(
            df = distplot_tbl(),
            source_name = "distplot",
            fill_colors = distplot_colors(),
            ylab = distplot_ylab(),
            xlab = distplot_xlab(),
            title = distplot_title()
        )
    })

    distplot_eventdata <- shiny::reactive({
        shiny::req(distplot_tbl(), distplot_function())
        plotly::event_data("plotly_click", "distplot")
    })

    shiny::callModule(
        plotly_server,
        "dist_plot",
        plot_tbl       = distplot_tbl,
        plot_eventdata = distplot_eventdata,
        group_tbl      = group_tbl,
    )

    # histplot ----------------------------------------------------------------

    histplot_title <- shiny::reactive({
        distplot_eventdata()$x[[1]]
    })

    histplot_tbl <- shiny::reactive({
        eventdata <- distplot_eventdata()
        shiny::validate(shiny::need(!is.null(eventdata), "Click plot above"))
        clicked_group <- eventdata$x[[1]]

        current_groups <- distplot_tbl() %>%
            dplyr::pull(x) %>%
            unique

        shiny::validate(
            shiny::need(clicked_group %in% current_groups, "Click plot above")
        )

        distplot_tbl() %>%
            dplyr::filter(x == clicked_group) %>%
            dplyr::select(x = y)
    })

    output$histplot <- plotly::renderPlotly({
        shiny::req(histplot_tbl())
        .GlobalEnv$create_histogram(
            df = histplot_tbl(),
            source_name = "histplot",
            x_lab = distplot_ylab(),
            title = histplot_title()
        )
    })

    shiny::callModule(
        plotly_server,
        "hist_plot",
        plot_tbl = histplot_tbl
    )


    # # source("R/functions/distribution_plot_module_functions.R", local = T)
    #
    # ns <- session$ns
    #
    # feature_groups <- shiny::reactive({
    #     shiny::req(feature_metadata_con())
    #     get_feature_group_names(feature_metadata_con())
    # })
    #
    # # determines if there are multiple ways to group input variables
    # multiple_variable_columns <- shiny::reactive({
    #     shiny::req(feature_groups())
    #     return(length(feature_groups()) > 1)
    # })
    #
    # # This is so that the conditional panel can see output$display_group_choice
    # output$display_group_choice <- shiny::reactive(multiple_variable_columns())
    # shiny::outputOptions(output, "display_group_choice", suspendWhenHidden = FALSE)
    #
    # # used when feature_metadata_con has more than one grouping column
    # output$group_choice_ui <- renderUI({
    #     shiny::req(feature_groups())
    #     shiny::selectInput(
    #         ns("group_choice"),
    #         label = "Select or Search for Group",
    #         choices = feature_groups())
    # })
    #
    # # used to determine what column to use for group choices
    # variable_choice_class_column <- shiny::reactive({
    #     if(multiple_variable_columns()){
    #         shiny::req(input$group_choice)
    #         return(input$group_choice)
    #     } else{
    #         return(3)
    #     }
    # })
    #
    # output$variable_choice_ui <- shiny::renderUI({
    #     shiny::req(variable_choice_class_column(), feature_metadata_con())
    #     choices <- create_nested_named_list(
    #         feature_metadata_con(),
    #         names_col1 = variable_choice_class_column(),
    #         names_col2 = "feature_name",
    #         values_col = "feature_id"
    #     )
    #     shiny::selectInput(
    #         ns("variable_choice_id"),
    #         label = "Select or Search for Variable",
    #         selected = variable_selection_default,
    #         choices = choices
    #     )
    # })
    #
    # distribution_plot_con <- shiny::reactive({
    #     shiny::req(
    #         feature_values_con(),
    #         input$variable_choice_id,
    #         input$scale_method
    #     )
    #
    #     feature_values_con() %>%
    #         dplyr::filter(feature_id == local(input$variable_choice_id)) %>%
    #         scale_db_connection(input$scale_method) %>%
    #         dplyr::select(label = sample_id, x = group, y = value)
    # })
    #
    # varible_display_name <- shiny::reactive({
    #     feature_metadata_con() %>%
    #         dplyr::filter(feature_id == local(input$variable_choice_id)) %>%
    #         dplyr::pull(feature_name)
    # })
    #
    # varible_plot_label <- shiny::reactive({
    #     transform_feature_string(varible_display_name(), input$scale_method)
    # })
    #
    # plot_function <- shiny::reactive({
    #     switch(
    #         input$plot_type,
    #         "Violin" = create_violinplot,
    #         "Box" = create_boxplot
    #     )
    # })
    #
    # output$plot <- plotly::renderPlotly({
    #     plot_function()(
    #         dplyr::collect(distribution_plot_con()),
    #         xlab = group_display_choice(),
    #         ylab = varible_plot_label(),
    #         title = varible_display_name(),
    #         source_name = plot_source_name,
    #         fill_colors = plot_colors(),
    #         ...)
    # })
    #
    # output$plot_text <- shiny::renderText({
    #     shiny::req(groups_con)
    #     eventdata <- plotly::event_data("plotly_click", source = plot_source_name)
    #     shiny::validate(shiny::need(eventdata, "Click above plot"))
    #
    #     groups_con() %>%
    #         dplyr::filter(group == local(unique(dplyr::pull(eventdata, "x")))) %>%
    #         dplyr::mutate(text = paste0(name, ": ", characteristics)) %>%
    #         dplyr::pull(text)
    #
    # })
    #
    # output$download_data <- shiny::downloadHandler(
    #     filename = function() stringr::str_c("data-", Sys.Date(), ".csv"),
    #     content = function(con) readr::write_csv(plot_df(), con)
    # )
    #
    # output$drilldown_plot <- plotly::renderPlotly({
    #
    #     eventdata <- plotly::event_data(
    #         "plotly_click",
    #         source = plot_source_name
    #     )
    #     shiny::validate(shiny::need(!is.null(eventdata), "Click plot above"))
    #     clicked_group <- eventdata$x[[1]]
    #
    #
    #     current_groups <- distribution_plot_con() %>%
    #         dplyr::pull(x) %>%
    #         unique
    #
    #     shiny::validate(
    #         shiny::need(clicked_group %in% current_groups, "Click plot above")
    #     )
    #
    #     distribution_plot_con() %>%
    #         dplyr::filter(x == clicked_group) %>%
    #         dplyr::select(x = y) %>%
    #         dplyr::collect() %>%
    #         create_histogram(
    #             title = clicked_group,
    #             x_lab = varible_plot_label()
    #         )
    # })
}
