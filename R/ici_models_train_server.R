ici_models_train_server <- function(
  id
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      list_format <- "<p style = 'color:Gray; font-size: 12px; height: 18px;'>"

      output$bucket_list <- shiny::renderUI({

        sortable::bucket_list(
          header = "Select training and testing datasets", #"",
          group_name = ns("dataset_bucket"),
          orientation = "horizontal",
          sortable::add_rank_list(
            text = "Datasets available",
            labels = lapply(
              lapply(names(datasets_options), function(x) paste(list_format, x, "</p>")),
              shiny::HTML),
            input_id = ns("datasets")
          ),
          sortable::add_rank_list(
            text = "Training datasets",
            input_id = ns("train")
          ),
          sortable::add_rank_list(
            text = "Testing datasets",
            input_id = ns("test")
          )
        )
      })

      predictors <- reactive({
        c(colnames(ioresponse_data$fmx_df), colnames(ioresponse_data$im_expr))
      })

      shiny::updateSelectizeInput(session, 'train_predictors',
                           choices = colnames(ioresponse_data$im_expr),
                           #selected = predictors()[19:25],
                           server = TRUE)


      output$samples_summary <- shiny::renderText({
        shiny::req(input$train)

        training <- ioresponse_data$fmx_df %>%
          dplyr::filter(Dataset %in% sapply(input$train, function(x) datasets_options[[x]]) & treatment_when_collected == "Pre") %>%
          nrow()

        testing <- ioresponse_data$fmx_df %>%
          dplyr::filter(Dataset %in% sapply(input$test, function(x) datasets_options[[x]]) & treatment_when_collected == "Pre") %>%
          nrow()

        paste("Samples in training set:",training,
              "Samples in testing set:", testing)

      })

      output$train_summary <- shiny::renderText({
        shiny::req(input$train_predictors)
        paste0("Selected formula: Response to ICI ~ ", paste(input$train_predictors, collapse = " + "))
      })
    }
  )
}
