volcano_plot_server <- function(
    input,
    output,
    session,
    volcano_plot_tbl,
    volcano_title,
    volcano_source_name,
    fold_change_num,
    fold_change_dem,
    response_id,
    pval_threshold = 0.05,
    fold_change_threshold = 2
){

    source("R/modules/server/submodules/plotly_server.R", local = T)
    source("R/driver_functions.R", local = T)

    output$volcano_plot <- plotly::renderPlotly({

        shiny::req(volcano_plot_tbl())

        shiny::validate(shiny::need(
            nrow(volcano_plot_tbl()) > 0,
            paste0(
                "Current parameters did not result in any linear regression",
                "results."
            )
        ))

        iatlas.app::create_scatterplot(
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

        clicked_label <- iatlas.app::get_values_from_eventdata(eventdata, "key")

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
        build_driver_violin_tbl(
            response_id(),
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

        ylab <- response_id() %>%
            as.integer() %>%
            iatlas.app::get_feature_display_from_id()

        title <- paste(
            "Cohort:",
            selected_volcano_result()$group, ";",
            "P-value:",
            round(selected_volcano_result()$p_value, 4), ";",
            "Log10(Fold Change):",
            round(selected_volcano_result()$log10_fold_change, 4)
        )

        iatlas.app::create_violinplot(
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
}
