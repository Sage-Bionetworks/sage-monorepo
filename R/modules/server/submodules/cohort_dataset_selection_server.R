cohort_dataset_selection_server <- function(
    input,
    output,
    session,
    default_dataset
){
    ns <- session$ns

    output$dataset_selection_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId  = ns("dataset_choice"),
            label    = "Select or Search for Dataset",
            choices  = tibble::deframe(iatlas.api.client::query_datasets()),
            selected = default_dataset
        )
    })

    output$module_availibility_string <- shiny::renderText({
        shiny::req(input$dataset_choice)
        iatlas.app::create_cohort_module_string(
            input$dataset_choice
        )
    })

    return(shiny::reactive(input$dataset_choice))
}
