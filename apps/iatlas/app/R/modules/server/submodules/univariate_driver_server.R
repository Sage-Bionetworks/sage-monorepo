univariate_driver_server <- function(
    input,
    output,
    session,
    cohort_obj
){
    ns <- session$ns

    source("R/modules/server/submodules/plotly_server.R", local = T)
    source("R/univariate_driver_functions.R", local = T)

    output$response_options <- shiny::renderUI({
        shiny::selectInput(
            inputId  = ns("response_variable"),
            label    = "Select or Search for Response Variable",
            choices = .GlobalEnv$create_nested_named_list(
                cohort_obj()$feature_tbl, values_col = "id"
            ),
            selected = .GlobalEnv$get_feature_id_from_display(
                "Leukocyte Fraction"
            )
        )
    })

    volcano_plot_tbl <- shiny::reactive({
        shiny::req(
            cohort_obj(),
            input$response_variable,
            input$min_wt,
            input$min_mut
        )

        build_ud_results_tbl(
            cohort_obj()$group_name,
            input$response_variable,
            input$min_wt,
            input$min_mut
        )
    })

    output$volcano_plot <- plotly::renderPlotly({

        shiny::req(volcano_plot_tbl())

        shiny::validate(shiny::need(
            nrow(volcano_plot_tbl()) > 0,
            paste0(
                "Current parameters did not result in any linear regression",
                "results."
            )
        ))

        .GlobalEnv$create_scatterplot(
            volcano_plot_tbl(),
            x_col     = "log10_fold_change",
            y_col     = "log10_p_value",
            xlab      = "Log10(Fold Change)",
            ylab      = "- Log10(P-value)",
            title     = "Immune Response Association With Driver Mutations",
            source    = "univariate_volcano_plot",
            key_col   = "label",
            label_col = "label",
            horizontal_line   = T,
            horizontal_line_y = (-log10(0.05))
        )
    })

    shiny::callModule(
        plotly_server,
        "volcano_plot",
        plot_tbl = volcano_plot_tbl
    )

    selected_volcano_result <- shiny::reactive({
        shiny::req(volcano_plot_tbl())

        eventdata <- plotly::event_data(
            "plotly_click",
            source = "univariate_volcano_plot"
        )

        # plot not clicked on yet
        shiny::validate(shiny::need(
            !is.null(eventdata),
            paste0(
                "Click a point on the above scatterplot to see a violin plot ",
                "for the comparison"
            )
        ))

        clicked_label <- .GlobalEnv$get_values_from_eventdata(eventdata, "key")

        result <-  dplyr::filter(
            volcano_plot_tbl(),
            label == clicked_label
        )

        #plot clicked on but event data stale due to parameter change
        shiny::validate(shiny::need(
            nrow(result) == 1,
            paste0(
                "Click a point on the above scatterplot to see a violin plot ",
                "for the comparison"
            )
        ))
        return(result)
    })

    violin_tbl <- shiny::reactive({
        shiny::req(selected_volcano_result())
        build_ud_driver_violin_tbl(
            input$response_variable,
            selected_volcano_result()$gene_id,
            selected_volcano_result()$tag_id,
            selected_volcano_result()$mutation_code_id
        )
    })

    output$violin_plot <- plotly::renderPlotly({

        xlab <- paste0(
            "Mutation Status ",
            selected_volcano_result()$gene,
            ":",
            selected_volcano_result()$mutation_code
        )

        ylab <- input$response_variable %>%
            as.integer() %>%
            .GlobalEnv$get_feature_display_from_id()

        title <- paste(
            "Cohort:",
            selected_volcano_result()$group, ";",
            "P-value:",
            round(selected_volcano_result()$p_value, 4), ";",
            "Log10(Fold Change):",
            round(selected_volcano_result()$log10_fold_change, 4)
        )

        .GlobalEnv$create_violinplot(
            violin_tbl(),
            xlab = xlab,
            ylab = ylab,
            title = title,
            fill_colors = c("blue"),
            showlegend = FALSE
        )
    })

    shiny::callModule(
        plotly_server,
        "violin_plot",
        plot_tbl = violin_tbl
    )

    # shiny::callModule(
    #     volcano_plot_server,
    #     "univariate_driver_server",
    #     volcano_plot_tbl,
    #     "Immune Response Association With Driver Mutations",
    #     "univariate_driver_server",
    #     "Wt",
    #     "Mut",
    #     shiny::reactive(input$response_variable)
    # )
}
