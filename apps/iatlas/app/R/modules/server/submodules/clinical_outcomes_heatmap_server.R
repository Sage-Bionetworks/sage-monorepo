clinical_outcomes_heatmap_server <- function(
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
            selected = "T Helper Cell Score"
        )
    })

    output$time_feature_selection_ui <- shiny::renderUI({
        choices <- cohort_obj()$feature_tbl %>%
            dplyr::filter(.data$class == "Survival Time") %>%
            dplyr::arrange(.data$order) %>%
            dplyr::select("display", "name") %>%
            tibble::deframe(.)

        shiny::selectInput(
            inputId = ns("time_feature_choice"),
            label = "Select or Search for Survival Endpoint",
            choices = choices
        )
    })

    status_feature_choice <- shiny::reactive({
        shiny::req(input$time_feature_choice)
        if (input$time_feature_choice == "PFI_time_1") return("PFI_1")
        else if (input$time_feature_choice == "OS_time") return("OS")
    })

    survival_value_tbl <- shiny::reactive({
        shiny::req(input$time_feature_choice, status_feature_choice())
        build_survival_value_tbl(
            cohort_obj()$sample_tbl,
            input$time_feature_choice,
            status_feature_choice()
        ) %>%
            dplyr::select(-"group")
    })

    feature_tbl <- shiny::reactive({
        shiny::req(input$class_choice)
        iatlas.app::query_features_values_by_tag(
            cohort_obj()$dataset,
            cohort_obj()$group_name,
            feature_class = input$class_choice
        ) %>%
            dplyr::rename("group" = "tag") %>%
            dplyr::filter(sample %in% cohort_obj()$sample_tbl$sample)
    })


    heatmap_tbl <- shiny::reactive({
        shiny::req(survival_value_tbl(), feature_tbl())
        dplyr::inner_join(survival_value_tbl(), feature_tbl(), by = "sample")
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
        shiny::req(heatmap_tbl())
        plotly::event_data("plotly_click", "clinical_outcomes_heatmap")
    })

    shiny::callModule(
        plotly_server,
        "heatmap",
        plot_tbl       = heatmap_tbl,
        plot_eventdata = heatmap_eventdata,
        group_tbl      = shiny::reactive(cohort_obj()$group_tbl)
    )
}
