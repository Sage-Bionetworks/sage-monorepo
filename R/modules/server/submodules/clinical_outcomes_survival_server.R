clinical_outcomes_survival_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    ns <- session$ns

    source("R/clinical_outcomes_functions.R")

    time_class_id <- iatlas.app::get_class_id_from_name("Survival Time")

    output$time_feature_selection_ui <- shiny::renderUI({
        shiny::req(time_class_id)

        shiny::selectInput(
            inputId = ns("time_feature_choice_id"),
            label = "Select or Search for Survival Endpoint",
            choices = iatlas.app::create_feature_named_list(time_class_id),
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

    survival_value_tbl <- shiny::reactive({
        shiny::req(
            cohort_obj(),
            time_feature_id(),
            status_feature_id()
        )
        build_survival_value_tbl(
            cohort_obj()$sample_tbl,
            time_feature_id(),
            status_feature_id()
        )
    })

    output$survival_plot <- shiny::renderPlot({

        shiny::req(
            survival_value_tbl(),
            cohort_obj(),
            input$risktable
        )

        shiny::validate(shiny::need(
            nrow(survival_value_tbl()) > 0,
            paste0(
                "Samples with selected variable don't have selected ",
                "survival features."
            )
        ))

        num_groups <- length(unique(survival_value_tbl()$group))

        shiny::validate(shiny::need(
            num_groups <= 10,
            paste0(
                "Too many sample groups (", num_groups, ") ",
                "for KM plot; choose a continuous variable or select ",
                "different sample groups."
            )
        ))

        fit <- survival::survfit(
            survival::Surv(time, status) ~ group,
            data = survival_value_tbl()
        )

        iatlas.app::create_kmplot(
            fit = fit,
            df = survival_value_tbl(),
            confint = input$confint,
            risktable = input$risktable,
            title = cohort_obj()$group_name,
            group_colors = unname(cohort_obj()$plot_colors)
        )
    })

    output$download_tbl <- shiny::downloadHandler(
        filename = function() stringr::str_c("data-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(survival_value_tbl(), con)
    )
}
