clinical_outcomes_heatmap_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl,
    group_name,
    plot_colors
){

    ns <- session$ns

    source("R/clinical_outcomes_functions.R")
    source("R/modules/server/submodules/plotly_server.R", local = T)

    output$class_selection_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId = ns("class_choice_id"),
            label = "Select or Search for Variables Class",
            choices = .GlobalEnv$create_class_list(),
            selected = .GlobalEnv$get_class_id_from_name("T Helper Cell Score")
        )
    })

    time_class_id <- .GlobalEnv$get_class_id_from_name("Survival Time")

    output$time_feature_selection_ui <- shiny::renderUI({
        shiny::req(time_class_id)

        shiny::selectInput(
            inputId = ns("time_feature_choice_id"),
            label = "Select or Search for Survival Endpoint",
            choices = .GlobalEnv$create_feature_named_list(time_class_id),
            selected = "OS Time"
        )
    })

    time_feature_id   <- shiny::reactive({
        shiny::req(input$time_feature_choice_id)
        as.integer(input$time_feature_choice_id)
    })

    status_feature_id <- shiny::reactive({
        shiny::req(time_feature_id())
        get_status_id_from_time_id(time_feature_id())
    })

    survival_tbl <- shiny::reactive({
        shiny::req(
            sample_tbl(),
            time_feature_id(),
            status_feature_id()
        )
        build_survival_value_tbl(
            sample_tbl(),
            time_feature_id(),
            status_feature_id()
        )
    })

    feature_tbl <- shiny::reactive({
        shiny::req(input$class_choice_id)
        build_feature_value_tbl_from_class_id(input$class_choice_id)
    })

    heatmap_tbl <- shiny::reactive({
        shiny::req(survival_tbl(), feature_tbl())
        dplyr::inner_join(survival_tbl(), feature_tbl(), by = "sample_id")
    })

    output$heatmap <- plotly::renderPlotly({
        shiny::req(heatmap_tbl())

        heatmap_matrix <- build_co_heatmap_matrix(heatmap_tbl())

        shiny::validate(shiny::need(
            nrow(heatmap_matrix > 0) & ncol(heatmap_matrix > 0),
            "No results to display, pick a different group."
        ))

        create_heatmap(heatmap_matrix, "clinical_outcomes_heatmap")
    })

    heatmap_eventdata <- shiny::reactive({
        plotly::event_data("plotly_click", "clinical_outcomes_heatmap")
    })

    shiny::callModule(
        plotly_server,
        "heatmap",
        plot_tbl       = heatmap_tbl,
        plot_eventdata = heatmap_eventdata,
        group_tbl      = group_tbl
    )
}
