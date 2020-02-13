univariate_driver_server <- function(
    input,
    output,
    session,
    group_name,
    feature_named_list
){
    ns <- session$ns

    source("R/modules/server/submodules/volcano_plot_server.R", local = T)
    source("R/functions/univariate_driver_functions.R", local = T)

    output$response_options <- shiny::renderUI({
        shiny::req(feature_named_list())
        shiny::selectInput(
            ns("response_variable"),
            "Select or Search for Response Variable",
            choices = feature_named_list(),
            selected = .GlobalEnv$get_feature_id("Leukocyte Fraction")
        )
    })

    results_tbl <- shiny::reactive({
        shiny::req(
            group_name(),
            input$response_variable,
            input$min_wt,
            input$min_mut
        )

        build_results_tbl(
            group_name(),
            input$response_variable,
            input$min_wt,
            input$min_mut
        )
    })

    volcano_tbl <- shiny::reactive({

        shiny::req(results_tbl())
        results_tbl() %>%
            dplyr::mutate(
                color = dplyr::if_else(
                    p_value < 0.05 &
                        abs(fold_change) > 2.0,
                    "red",
                    "blue"
                )
            )
    })

    output$volcano_plot <- plotly::renderPlotly({

        shiny::req(volcano_tbl())

        shiny::validate(shiny::need(
            nrow(volcano_tbl()) > 0,
            "Current parameters did not result in any successful linear regression results."
        ))

        .GlobalEnv$create_scatterplot(
            volcano_tbl(),
            x_col = "log10_fold_change",
            y_col = "log10_p_value",
            xlab = "Log10(Fold Change)",
            ylab = "- Log10(P-value)",
            title = "Immune Response Association With Driver Mutations",
            source = "univariate_volcano_plot",
            key_col = "row_n",
            label_col = "label",
            color_col = "color",
            fill_colors = c("blue" = "blue", "red" = "red")
        )
    })

    output$violin_plot <- plotly::renderPlotly({

        shiny::req(volcano_tbl())

        eventdata <- plotly::event_data(
            "plotly_click",
            source = "univariate_volcano_plot"
        )

        # plot not clicked on yet
        shiny::validate(shiny::need(
            !is.null(eventdata),
            "Click a point on the above scatterplot to see a violin plot for the comparison"
        ))

        result <-  dplyr::filter(
            volcano_tbl(),
            row_n == eventdata$key[[1]]
        )

        #plot clicked on but event data stale due to parameter change
        shiny::validate(shiny::need(
            nrow(result) == 1,
            "Click a point on the above scatterplot to see a violin plot for the comparison"
        ))


        tbl <- build_violin_tbl(
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
