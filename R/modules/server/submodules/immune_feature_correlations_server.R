immune_feature_correlations_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl,
    feature_named_list
){

    ns <- session$ns

    source("R/immune_feature_correlations_functions.R", local = T)
    source("R/modules/server/submodules/plotly_server.R", local = T)

    output$class_selection_ui <- shiny::renderUI({
        shiny::selectInput(
            ns("class_choice_id"),
            "Select or Search for Variable Class",
            selected = .GlobalEnv$get_class_id_from_name("DNA Alteration"),
            choices = .GlobalEnv$create_class_list()
        )
    })

    output$response_selection_ui <- shiny::renderUI({
        shiny::req(feature_named_list())
        shiny::selectInput(
            ns("response_choice_id"),
            "Select or Search for Response Variable",
            choices = feature_named_list(),
            selected = .GlobalEnv$get_feature_id_from_display(
                "Leukocyte Fraction"
            ),
        )
    })

    response_name <- shiny::reactive({
        shiny::req(input$response_choice_id)
        input$response_choice_id %>%
            as.integer() %>%
            .GlobalEnv$get_feature_display_from_id()
    })

    response_tbl <- shiny::reactive({
        shiny::req(input$response_choice_id)
        .GlobalEnv$build_feature_value_tbl_from_ids(input$response_choice_id)
    })

    feature_tbl <- shiny::reactive({
        shiny::req(input$class_choice_id)
        .GlobalEnv$build_feature_value_tbl_from_class_ids(input$class_choice_id)
    })

    value_tbl <- shiny::reactive({
        shiny::req(response_tbl(), feature_tbl(), sample_tbl())
        build_ifc_value_tbl(response_tbl(), feature_tbl(), sample_tbl())
    })

    heatmap_matrix <- shiny::reactive({
        shiny::req(value_tbl(),  input$correlation_method)
        build_ifc_heatmap_matrix(value_tbl(), input$correlation_method)
    })

    output$heatmap <- plotly::renderPlotly({
        shiny::req(heatmap_matrix())
        create_heatmap(
            heatmap_matrix(),
            "immune_features_heatmap",
            scale_colors = T
        )
    })

    heatmap_eventdata <- shiny::reactive({
        plotly::event_data("plotly_click", "immune_features_heatmap")
    })

    shiny::callModule(
        plotly_server,
        "heatmap",
        plot_tbl       = heatmap_matrix,
        plot_eventdata = heatmap_eventdata,
        group_tbl      = group_tbl
    )

    scatterplot_tbl <- shiny::reactive({
        eventdata <- heatmap_eventdata()
        shiny::validate(shiny::need(eventdata, "Click above heatmap"))
        clicked_group <- get_values_from_eventdata(eventdata)
        clicked_feature <- get_values_from_eventdata(eventdata, "y")

        value_tbl() %>%
            build_ifc_scatterplot_tbl(clicked_feature, clicked_group) %>%
            create_scatterplot(
                xlab =  clicked_feature,
                ylab =  response_name(),
                title = clicked_group,
                label_col = "label",
                fill_colors = "blue"
            )
    })

    output$scatterPlot <- plotly::renderPlotly({
        shiny::req(value_tbl(), response_name())

        eventdata <- heatmap_eventdata()
        shiny::validate(shiny::need(eventdata, "Click above heatmap"))

        clicked_group <- eventdata$x[[1]]
        clicked_feature <- eventdata$y[[1]]


        shiny::validate(shiny::need(
            all(
                clicked_feature %in% value_tbl()$feature_name,
                clicked_group %in% value_tbl()$group
            ),
            "Click above heatmap"
        ))
        scatterplot_tbl()
    })

    shiny::callModule(
        plotly_server,
        "scatterplot",
        plot_tbl = scatterplot_tbl
    )
}

