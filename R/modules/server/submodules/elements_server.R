# used in cohort selection ----------------------------------------------------

source("R/elements_functions.R", local = T)

numeric_filter_element_server <- function(
    input,
    output,
    session,
    reactive_values,
    module_id,
    feature_named_list
){

    ns <- session$ns

    output$select_ui <- shiny::renderUI({
        shiny::req(feature_named_list())
        shiny::selectInput(
            inputId = ns("feature_choice"),
            label = "Select or Search for feature",
            choices = feature_named_list()
        )
    })

    features_tbl <- shiny::reactive({
        req(input$feature_choice)
        build_numeric_filter_tbl(input$feature_choice)
    })

    feature_min <- shiny::reactive({
        shiny::req(features_tbl())
        round(features_tbl()$feature_min, 2)
    })

    feature_max <- shiny::reactive({
        shiny::req(features_tbl())
        round(features_tbl()$feature_max, 2)
    })

    output$slider_ui <- shiny::renderUI({
        shiny::req(feature_max(), feature_min())

        shiny::sliderInput(
            inputId = ns("range"),
            label = "Filter:",
            min = round(feature_min()),
            max = round(feature_max()),
            value = c(feature_min(), feature_max())
        )
    })

    shiny::observeEvent(input$feature_choice, {
        reactive_values[[module_id]]$feature_choice <- input$feature_choice
    })

    shiny::observeEvent(input$range, {
        reactive_values[[module_id]]$feature_range <- input$range
    })

    return(reactive_values)
}

group_filter_element_server <- function(
    input,
    output,
    session,
    reactive_values,
    module_id,
    group_named_list
){

    ns <- session$ns

    output$select_ui <- shiny::renderUI({
        shiny::req(group_named_list())
        shiny::selectInput(
            inputId = ns("parent_group_choice_id"),
            label = "Select or Search for Group",
            choices = group_named_list()
        )
    })

    output$checkbox_ui <- shiny::renderUI({
        shiny::req(input$parent_group_choice_id)
        shiny::checkboxGroupInput(
            inputId = ns("group_choice_ids"),
            label = "Select choices to include:",
            choices = build_group_filter_tbl(input$parent_group_choice_id),
            inline = T
        )
    })

    shiny::observeEvent(input$group_choice_ids, {
        reactive_values[[module_id]]$group_choice_ids <- input$group_choice_ids
    })

    return(reactive_values)
}

# used in driver module -------------------------------------------------------

numeric_model_covariate_element_server <- function(
    input,
    output,
    session,
    reactive_values,
    module_id,
    covariate_tbl
){

    ns <- session$ns

    output$select_covariate_ui <- shiny::renderUI({
        shiny::req(covariate_tbl())
        choices <- create_nested_named_list(covariate_tbl())
        shiny::selectInput(
            inputId = ns("covariate_choice_id"),
            label   = "Select or Search for Covariate",
            choices = choices
        )
    })

    output$select_transformation_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId = ns("transformation_choice"),
            label   = "Select or Search for Transformation",
            choices = c("None", "Squared", "Log10", "Reciprocal")
        )
    })

    shiny::observeEvent(input$covariate_choice_id, {
        reactive_values[[module_id]]$covariate_choice_id <-
            as.numeric(input$covariate_choice_id)
    })

    shiny::observeEvent(input$transformation_choice, {
        reactive_values[[module_id]]$transformation_choice <-
            input$transformation_choice
    })

    return(reactive_values)
}


categorical_model_covariate_element_server <- function(
    input,
    output,
    session,
    reactive_values,
    module_id,
    covariate_tbl
){

    ns <- session$ns

    output$select_covariate_ui <- shiny::renderUI({
        shiny::req(covariate_tbl())
        choices <- create_nested_named_list(covariate_tbl())
        shiny::selectInput(
            inputId = ns("covariate_choice"),
            label   = "Select or Search for Covariate",
            choices = choices
        )
    })

    shiny::observeEvent(input$covariate_choice, {
        reactive_values[[module_id]]$covariate_choice <- input$covariate_choice
    })

    return(reactive_values)
}
