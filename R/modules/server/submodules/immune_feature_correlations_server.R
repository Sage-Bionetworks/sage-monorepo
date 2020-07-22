immune_feature_correlations_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    ns <- session$ns

    source("R/modules/server/submodules/plotly_server.R", local = T)

    output$class_selection_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId  = ns("class_choice"),
            label    = "Select or Search for Variable Class",
            choices  = cohort_obj() %>%
                purrr::pluck("feature_tbl") %>%
                dplyr::pull("class") %>%
                unique() %>%
                sort(),
            selected = "DNA Alteration"
        )
    })

    output$response_selection_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId  = ns("response_choice"),
            label    = "Select or Search for Response Variable",
            choices = iatlas.app::create_nested_named_list(
                cohort_obj()$feature_tbl, values_col = "name"
            ),
            selected = "leukocyte_fraction"
        )
    })

    response_choice_display <- shiny::reactive({
        shiny::req(input$response_choice)
        cohort_obj()$feature_tbl %>%
            dplyr::filter(name == input$response_choice) %>%
            dplyr::pull(display)
    })

    response_tbl <- shiny::reactive({
        shiny::req(input$response_choice)
        build_ifc_response_tbl(cohort_obj(), input$response_choice)
    })

    feature_tbl <- shiny::reactive({
        shiny::req(input$class_choice)
        build_ifc_feature_tbl(cohort_obj(), input$class_choice)
    })

    value_tbl <- shiny::reactive({
        shiny::req(response_tbl(), feature_tbl())
        build_ifc_value_tbl(
            response_tbl(),
            feature_tbl(),
            cohort_obj()$sample_tbl
        )
    })

    heatmap_matrix <- shiny::reactive({
        shiny::req(value_tbl(), input$correlation_method)
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
        shiny::req(heatmap_matrix())
        plotly::event_data("plotly_click", "immune_features_heatmap")
    })

    shiny::callModule(
        plotly_server,
        "heatmap",
        plot_tbl       = heatmap_matrix,
        plot_eventdata = heatmap_eventdata,
        group_tbl      = shiny::reactive(cohort_obj()$group_tbl)
    )

    scatterplot_tbl <- shiny::reactive({
        shiny::req(value_tbl())
        shiny::validate(shiny::need(heatmap_eventdata(), "Click above heatmap"))
        group   <- iatlas.app::get_values_from_eventdata(heatmap_eventdata())
        feature <- iatlas.app::get_values_from_eventdata(
            heatmap_eventdata(), "y"
        )
        iatlas.app::build_ifc_scatterplot_tbl(value_tbl(), feature, group)
    })

    output$scatterPlot <- plotly::renderPlotly({
        shiny::req(value_tbl(), scatterplot_tbl(), response_choice_display())
        shiny::validate(shiny::need(heatmap_eventdata(), "Click above heatmap"))

        clicked_group <- heatmap_eventdata()$x[[1]]
        clicked_feature <- heatmap_eventdata()$y[[1]]

        shiny::validate(shiny::need(
            all(
                clicked_feature %in% value_tbl()$feature_name,
                clicked_group %in% value_tbl()$group
            ),
            "Click above heatmap"
        ))

        create_scatterplot(
            scatterplot_tbl(),
            xlab =  clicked_feature,
            ylab =  response_choice_display(),
            title = clicked_group,
            label_col = "label",
            fill_colors = "blue"
        )
    })

    shiny::callModule(
        plotly_server,
        "scatterplot",
        plot_tbl = scatterplot_tbl
    )
}

