# used in cohort selection ----------------------------------------------------

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
        req(input$feature_choice_id)
        build_numeric_filter_tbl(input$feature_choice_id)
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

    shiny::observeEvent(input$feature_choice_id, {
        reactive_values[[module_id]]$id <- input$feature_choice_id
    })

    shiny::observeEvent(input$range, {
        reactive_values[[module_id]]$min <- input$range[[1]]
        reactive_values[[module_id]]$max <- input$range[[2]]
    })

    return(reactive_values)
}

tag_filter_element_server <- function(
    input,
    output,
    session,
    reactive_values,
    module_id,
    tag_named_list
){

    ns <- session$ns

    output$select_ui <- shiny::renderUI({
        shiny::req(tag_named_list())
        shiny::selectInput(
            inputId = ns("parent_tag_choice"),
            label = "Select or Search for Group",
            choices = tag_named_list()
        )
    })

    output$checkbox_ui <- shiny::renderUI({
        shiny::req(input$parent_tag_choice)
        shiny::checkboxGroupInput(
            inputId = ns("tag_choices"),
            label = "Select choices to include:",
            choices = iatlas.app::build_tag_filter_named_list(
                input$parent_tag_choice
            ),
            inline = T
        )
    })

    shiny::observeEvent(input$tag_choices, {
        reactive_values[[module_id]]$tags <- input$tag_choices
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
    covariate_list
){

    ns <- session$ns

    output$select_covariate_ui <- shiny::renderUI({
        shiny::req(covariate_list())
        shiny::selectInput(
            inputId = ns("covariate_choice_name"),
            label   = "Select or Search for Covariate",
            choices = covariate_list()
        )
    })

    output$select_transformation_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId = ns("transformation_choice"),
            label   = "Select or Search for Transformation",
            choices = c("None", "Squared", "Log10", "Reciprocal")
        )
    })

    shiny::observeEvent(input$covariate_choice_name, {
        reactive_values[[module_id]]$covariate_choice_name <-
            input$covariate_choice_name
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
    covariate_list
){

    ns <- session$ns

    output$select_covariate_ui <- shiny::renderUI({
        shiny::req(covariate_list())
        shiny::selectInput(
            inputId = ns("covariate_choice"),
            label   = "Select or Search for Covariate",
            choices = covariate_list()
        )
    })

    shiny::observeEvent(input$covariate_choice, {
        reactive_values[[module_id]]$covariate_choice <- input$covariate_choice
    })

    return(reactive_values)
}
