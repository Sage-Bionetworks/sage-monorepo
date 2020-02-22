overall_cell_proportions_server  <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl
){

    source("R/modules/server/submodules/plotly_server.R", local = T)
    source("R/overall_cell_proportions_functions.R", local = T)

    value_tbl <- shiny::reactive({
        shiny::req(sample_tbl())
        build_value_tbl(sample_tbl())
    })

    barplot_tbl <- shiny::reactive({
        req(value_tbl())
        build_barplot_tbl(value_tbl())
    })

    output$barplot <- plotly::renderPlotly({
        shiny::req(barplot_tbl())

        .GlobalEnv$create_barplot(
            barplot_tbl(),
            source_name = "overall_cell_proportions_barplot",
            color_col = "color",
            label_col = "label",
            xlab = "Fraction type by group",
            ylab = "Fraction mean"
        )
    })

    barplot_eventdata <- shiny::reactive({
        plotly::event_data("plotly_click", "overall_cell_proportions_barplot")
    })

    shiny::callModule(
        plotly_server,
        "barplot",
        plot_tbl       = barplot_tbl,
        plot_eventdata = barplot_eventdata,
        group_tbl      = group_tbl,
    )

    barplot_selected_group <- shiny::reactive({
        shiny::req(barplot_eventdata())
        selected_group <- barplot_eventdata()$x[[1]]
    })

    scatterplot_tbl <- shiny::reactive({
        shiny::req(value_tbl(), barplot_selected_group())
        build_scatterplot_tbl(value_tbl(), barplot_selected_group())
    })

    output$scatterplot <- plotly::renderPlotly({
        shiny::validate(shiny::need(barplot_eventdata(), "Click above plot"))
        shiny::req(value_tbl())

        groups <- dplyr::pull(value_tbl(), group)
        shiny::validate(shiny::need(
            barplot_selected_group() %in% groups,
            "Click above barchart"
        ))
        .GlobalEnv$create_scatterplot(
            scatterplot_tbl(),
            source_name = "overall_cell_proportions_scatterplot",
            xlab = "Stromal Fraction",
            ylab = "Leukocyte Fraction",
            label_col = "label",
            title = barplot_selected_group(),
            identity_line = TRUE
        )
    })

    shiny::callModule(
        plotly_server,
        "scatterplot",
        plot_tbl = barplot_tbl
    )
}
