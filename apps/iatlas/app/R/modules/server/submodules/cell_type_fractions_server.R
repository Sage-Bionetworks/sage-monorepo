cell_type_fractions_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl
){

    source("R/modules/server/submodules/plotly_server.R", local = T)
    source("R/cell_type_fractions_functions.R", local = T)

    plot_tbl <- shiny::reactive({
        shiny::req(input$fraction_group_choice, sample_tbl())
        build_ctf_barplot_tbl(input$fraction_group_choice, sample_tbl())
    })

    output$barplot <- plotly::renderPlotly({
        shiny::req(plot_tbl())

        .GlobalEnv$create_barplot(
            plot_tbl(),
            source_name = "cell_type_fractions_barplot",
            color_col = "color",
            label_col = "label",
            xlab = "Fraction type by group",
            ylab = "Fraction mean"
        )
    })

    barplot_eventdata <- shiny::reactive({
        plotly::event_data("plotly_click", "cell_type_fractions_barplot")
    })

    shiny::callModule(
        plotly_server,
        "barplot",
        plot_tbl       = plot_tbl,
        plot_eventdata = barplot_eventdata,
        group_tbl      = group_tbl
    )
}
