univariate_driver_server <- function(
    input,
    output,
    session,
    group_name
){
    ns <- session$ns

    source("R/modules/server/submodules/volcano_plot_server.R", local = T)
    source("R/univariate_driver_functions.R", local = T)

    output$response_options <- shiny::renderUI({
        shiny::selectInput(
            inputId  = ns("response_variable"),
            label    = "Select or Search for Response Variable",
            choices  = .GlobalEnv$create_feature_named_list(),
            selected = .GlobalEnv$get_feature_id_from_display(
                "Leukocyte Fraction"
            )
        )
    })

    volcano_plot_tbl <- shiny::reactive({
        shiny::req(
            group_name(),
            input$response_variable,
            input$min_wt,
            input$min_mut
        )

        build_udr_results_tbl(
            group_name(),
            input$response_variable,
            input$min_wt,
            input$min_mut
        )
    })

    output$volcano_plot <- plotly::renderPlotly({

        shiny::req(volcano_plot_tbl())

        shiny::validate(shiny::need(
            nrow(volcano_plot_tbl()) > 0,
            "Current parameters did not result in any linear regression results."
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
            horizontal_line_y = (- log10(0.05))
        )
    })

    output$violin_plot <- plotly::renderPlotly({

        shiny::req(volcano_plot_tbl())

        eventdata <- plotly::event_data(
            "plotly_click",
            source = "univariate_volcano_plot"
        )

        # plot not clicked on yet
        shiny::validate(shiny::need(
            !is.null(eventdata),
            "Click a point on the above scatterplot to see a violin plot for the comparison"
        ))

        print(eventdata)
        clicked_label <- .GlobalEnv$get_values_from_eventdata(eventdata, "key")

        result <-  dplyr::filter(
            volcano_plot_tbl(),
            label == clicked_label
        )

        print(result)

        #plot clicked on but event data stale due to parameter change
        shiny::validate(shiny::need(
            nrow(result) == 1,
            "Click a point on the above scatterplot to see a violin plot for the comparison"
        ))

        build_udr_violin_tbl(
            input$response_variable,
            result$gene_id,
            result$tag_id
        )

        xlab <- paste(result$gene, "mutation_status")
        ylab <- .GlobalEnv$get_feature_name(input$response_variable)
        title <- paste(
            "Cohort:", result$group, ";",
            "P-value:", round(result$p_value, 4), ";",
            "Log10(Fold Change):", round(result$log10_fold_change, 4)
        )

        .GlobalEnv$create_violinplot(
            tbl,
            xlab = xlab,
            ylab = ylab,
            title = title,
            fill_colors = c("blue"),
            showlegend = FALSE
        )
    })
}
