cell_type_fractions_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    source("R/modules/server/submodules/plotly_server.R", local = T)

    data_tbl <- shiny::reactive({
        shiny::req(input$fraction_group_choice)
        iatlas.app::query_features_values_by_tag(
            cohort_obj()$dataset,
            cohort_obj()$group_name,
            feature_class = input$fraction_group_choice
        )
    })

    plot_tbl <- shiny::reactive({
        shiny::req(data_tbl())
        iatlas.app::build_ctf_barplot_tbl(data_tbl())
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
        group_tbl      = shiny::reactive(cohort_obj()$group_tbl)
    )
}
