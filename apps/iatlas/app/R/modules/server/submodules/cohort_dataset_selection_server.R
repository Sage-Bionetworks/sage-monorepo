cohort_dataset_selection_server <- function(
    input,
    output,
    session,
    default_dataset
){
    ns <- session$ns

    source("R/cohort_dataset_selection_functions.R", local = T)

    dataset_to_module_tbl <- dplyr::tribble(
        ~module,                  ~dataset,
        "Sample Group Overview",  "TCGA",
        "Tumor Microenvironment", "TCGA",
        "Immune Feature Trends",  "TCGA",
        "Clinical Outcomes",      "TCGA",
        "IO Targets",             "TCGA",
        "TIL Maps",               "TCGA",
        "Driver Associations",    "TCGA"
        # "Sample Group Overview",  "PCAWG",
        # "Tumor Microenvironment", "PCAWG",
        # "Immune Feature Trends",  "PCAWG",
        # "IO Targets",             "PCAWG",
        # "Driver Associations",    "PCAWG"
    )

    output$dataset_selection_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId  = ns("dataset_choice"),
            label    = "Select or Search for Dataset",
            choices  = unique(dataset_to_module_tbl$dataset),
            selected = default_dataset
        )
    })

    output$module_availibility_string <- shiny::renderText({
        shiny::req(input$dataset_choice)
        create_cohort_module_string(dataset_to_module_tbl, input$dataset_choice)
    })

    return(shiny::reactive(input$dataset_choice))
}
