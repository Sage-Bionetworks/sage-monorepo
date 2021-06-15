# used in cohort selection ----------------------------------------------------

numeric_filter_element_server <- function(
  id,
  reactive_values,
  module_id,
  numeric_named_list,
  dataset
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$select_ui <- shiny::renderUI({
        shiny::req(numeric_named_list())
        shiny::selectInput(
          inputId = ns("numeric_choice"),
          label = "Select or Search for feature",
          choices = numeric_named_list()
        )
      })

      numeric_type <- shiny::reactive({
        shiny::req(input$numeric_choice)
        stringr::str_remove_all(input$numeric_choice, ":[:print:]+$")
      })

      numeric_name <- shiny::reactive({
        shiny::req(input$numeric_choice)
        stringr::str_remove_all(input$numeric_choice, "^[:print:]+:")
      })

      features_tbl <- shiny::reactive({
        req(numeric_type(), numeric_name(), dataset())
        if(numeric_type() == "feature"){
          tbl <-
            iatlas.api.client::query_features_range(
              cohorts = dataset(),
              features = numeric_name()
            ) %>%
            dplyr::distinct()
        } else if (numeric_type() == "clinical"){
          tbl <-
            iatlas.api.client::query_patients(datasets = dataset()) %>%
            dplyr::select("value" = numeric_name()) %>%
            tidyr::drop_na() %>%
            dplyr::summarise(
              "value_min" = min(.data$value), "value_max" = max(.data$value)
            )
        }
        return(tbl)
      })

      feature_min <- shiny::reactive({
        shiny::req(features_tbl())
        features_tbl() %>%
          dplyr::pull(value_min) %>%
          round(2)
      })

      feature_max <- shiny::reactive({
        shiny::req(features_tbl())
        features_tbl() %>%
          dplyr::pull(value_max) %>%
          round(2)
      })

      output$slider_ui <- shiny::renderUI({
        shiny::req(feature_max(), feature_min())

        shiny::sliderInput(
          inputId = ns("range"),
          label = "Filter:",
          min = feature_min(),
          max = feature_max(),
          value = c(feature_min(), feature_max())
        )
      })

      shiny::observeEvent(numeric_type(), {
        reactive_values[[module_id]]$type <- numeric_type()
      })

      shiny::observeEvent(numeric_name(), {
        reactive_values[[module_id]]$name <- numeric_name()
      })

      shiny::observeEvent(input$range, {
        reactive_values[[module_id]]$min <- input$range[[1]]
        reactive_values[[module_id]]$max <- input$range[[2]]
      })

      return(reactive_values)
    }
  )
}

group_filter_element_server <- function(
  id,
  reactive_values,
  module_id,
  group_named_list,
  dataset
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$select_ui <- shiny::renderUI({
        shiny::req(group_named_list())
        shiny::selectInput(
          inputId = ns("parent_group_choice"),
          label = "Select or Search for Group",
          choices = group_named_list()
        )
      })

      group_type <- shiny::reactive({
        shiny::req(input$parent_group_choice)
        stringr::str_remove_all(input$parent_group_choice, ":[:print:]+$")
      })

      parent_group <- shiny::reactive({
        shiny::req(input$parent_group_choice)
        stringr::str_remove_all(input$parent_group_choice, "^[:print:]+:")
      })

      group_choices <- shiny::reactive({
        shiny::req(group_type(), parent_group(), input$parent_group_choice)
        if(group_type() == "tag"){
          choices <- build_tag_filter_list(parent_group(), dataset())
        } else if (group_type() == "clinical"){
          choices <-
            iatlas.api.client::query_patients(datasets = dataset()) %>%
            dplyr::select(parent_group()) %>%
            dplyr::distinct() %>%
            tidyr::drop_na() %>%
            dplyr::pull(parent_group())
        }
        return(choices)
      })

      output$checkbox_ui <- shiny::renderUI({
        shiny::req(group_choices())
        shiny::checkboxGroupInput(
          inputId = ns("group_choices"),
          label = "Select choices to include:",
          choices = group_choices(),
          inline = T
        )
      })

      shiny::observeEvent(parent_group(), {
        reactive_values[[module_id]]$parent_group_choice <- parent_group()
      })

      shiny::observeEvent(group_type(), {
        reactive_values[[module_id]]$group_type <- group_type()
      })

      shiny::observeEvent(input$group_choices, {
        reactive_values[[module_id]]$group_choices <- input$group_choices
      })

      return(reactive_values)
    }
  )
}

# used in driver module -------------------------------------------------------

numeric_model_covariate_element_server <- function(
  id,
  reactive_values,
  module_id,
  covariate_list
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

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
  )
}

categorical_model_covariate_element_server <- function(
  id,
  reactive_values,
  module_id,
  covariate_list
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

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
  )
}
