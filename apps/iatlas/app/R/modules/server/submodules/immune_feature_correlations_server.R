immune_feature_correlations_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    ns <- session$ns

    source("R/immune_feature_correlations_functions.R", local = T)
    source("R/modules/server/submodules/plotly_server.R", local = T)

    lf_id <- .GlobalEnv$get_feature_id_from_display("Leukocyte Fraction")
    dna_alteration_id <- .GlobalEnv$get_class_id_from_name("DNA Alteration")
    sample_name_tbl   <- .GlobalEnv$build_sample_name_tbl()
    class_list        <- .GlobalEnv$create_class_list()

    output$class_selection_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId  = ns("class_choice_id"),
            label    = "Select or Search for Variable Class",
            choices  = class_list,
            selected = dna_alteration_id
        )
    })

    output$response_selection_ui <- shiny::renderUI({
        shiny::req(cohort_obj())
        shiny::selectInput(
            inputId  = ns("response_choice_id"),
            label    = "Select or Search for Response Variable",
            choices  = .GlobalEnv$create_nested_named_list(
                cohort_obj()$feature_tbl, values_col = "id"
            ),
            selected = lf_id
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
        shiny::req(response_tbl(), feature_tbl(), cohort_obj())
        build_ifc_value_tbl(
            response_tbl(),
            feature_tbl(),
            cohort_obj()$sample_tbl
        )
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
        group <- get_values_from_eventdata(heatmap_eventdata())
        feature <- get_values_from_eventdata(heatmap_eventdata(), "y")

         build_ifc_scatterplot_tbl(
             value_tbl(),
             sample_name_tbl,
             feature,
             group
         )
    })

    output$scatterPlot <- plotly::renderPlotly({
        shiny::req(value_tbl(), scatterplot_tbl(), response_name())
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
            ylab =  response_name(),
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

