univariate_driver_server <- function(
    input,
    output,
    session,
    group_name
){
    ns <- session$ns

    source("R/modules/server/submodules/volcano_plot_server.R", local = T)
    source("R/driver_functions.R", local = T)

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

    shiny::callModule(
        volcano_plot_server,
        "univariate_driver_server",
        volcano_plot_tbl,
        "Immune Response Association With Driver Mutations",
        "univariate_driver_server",
        "Wt",
        "Mut",
        shiny::reactive(input$response_variable)
    )
}
