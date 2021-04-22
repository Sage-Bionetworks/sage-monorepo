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

      train_ds <- reactive({
        get_dataset_id(input$train)
      })

      test_ds <- reactive({
        get_dataset_id(input$test)
      })


      output$samples_summary <- shiny::renderText({
        shiny::req(input$train)

        training <- ioresponse_data$fmx_df %>%
          dplyr::filter(Dataset %in% train_ds() & treatment_when_collected == "Pre") %>%
          nrow()

        testing <- ioresponse_data$fmx_df %>%
          dplyr::filter(Dataset %in% test_ds() & treatment_when_collected == "Pre") %>%
          nrow()

        paste("Samples in training set:",training,
              "Samples in testing set:", testing)

      })

      output$train_summary <- shiny::renderText({
        shiny::req(input$train_predictors)
        paste0("Selected formula: Response to ICI ~ ", paste(input$train_predictors, collapse = " + "))
      })

      #organizing train and test datasets - subsetting and normalizing

      df_to_model <- reactive({
        ioresponse_data$fmx_df %>%
          dplyr::filter(treatment_when_collected == "Pre") %>%
          tidyr::drop_na("Responder") %>% #excluding samples that don't have response annotation
          merge(., ioresponse_data$im_expr, by = "Sample_ID")
      })

      train_df <- eventReactive(input$compute_train,{
        normalize_dataset(
          df = df_to_model(),
          train_ds = train_ds(),
          test_ds = test_ds(),
          variable_to_norm = input$train_predictors,
          predictors = input$train_predictors,
          is_test = FALSE
        )
      })

      test_df <- eventReactive(input$compute_test,{
        shiny::req(model_train())
        normalize_dataset(
          df = df_to_model(),
          train_ds = train_ds(),
          test_ds = test_ds(),
          variable_to_norm = input$train_predictors,
          predictors = input$train_predictors,
          is_test = TRUE
        )
      })

      #Running model
      model_train <- eventReactive(input$compute_train, {
        run_elastic_net(
          train_df = train_df(),
          response_variable = "Responder",
          predictors = input$train_predictors,
          n_cv_folds = input$cv_number
        )
      })

      output$results <- DT::renderDataTable(
        model_train()$results[rownames(model_train()$bestTune),]
      )

      output$plot_coef <- plotly::renderPlotly({
        plot_df <- data.frame(
          x = coef(model_train()$finalModel, model_train()$bestTune$lambda)@x,
          y = coef(model_train()$finalModel,
                   model_train()$bestTune$lambda)@Dimnames[[1]][coef(model_train()$finalModel, model_train()$bestTune$lambda)@i+1],
          error = 0
        )

        plot_levels <-levels(reorder(plot_df[["y"]], plot_df[["x"]], sort))

        create_barplot_horizontal(
          df = plot_df,
          x_col = "x",
          y_col = "y",
          error_col = "error",
          key_col = NA,
          color_col = NA,
          label_col = NA,
          order_by = plot_levels,
          xlab = "",
          ylab = "",
          title = "",
          showLegend = FALSE,
          source_name = NULL,
          bar_colors = "gray"
        )
      })

      prediction_test <- eventReactive(input$compute_test, {
        predict(model_train(), newdata = test_df())
      })

      output$confusion_matrix <- DT::renderDataTable({
        shiny::req(prediction_test())
        table(
          test_df()$Responder,
          prediction_test()
        )
      })

      output$accuracy <- renderText({
        shiny::req(prediction_test())
        accuracy <- mean(test_df()$Responder == prediction_test())
        paste("Accuracy: ", accuracy)
      })

      output$roc <- renderPlot({
        rplot <- pROC::roc(
          response = factor(test_df()$Responder,  ordered = TRUE),
          predictor = factor(prediction_test(), ordered = TRUE)
        )
        plot(rplot, print.auc = TRUE)
      })

      #code for km plot
#
      all_survival <- reactive({
        shiny::req(prediction_test())
        df_km <- merge(cbind(test_df(), prediction = prediction_test()), df_to_model() %>% select(Sample_ID, "OS_time", "OS"), by = "Sample_ID")

        df <- purrr::map(.x = test_ds(), df = df_km, .f= function(dataset, df){
          dataset_df <- df %>%
            dplyr::filter(Dataset == dataset)

          build_survival_df(
            df = dataset_df,
            group_column = "prediction",
            group_options = "prediction",
            time_column = "OS_time"
          )
        })
      })#all_survival

      all_fit <- reactive({
        purrr::map(all_survival(), function(df) survival::survfit(survival::Surv(time, status) ~ variable, data = df))
      })

      all_kmplot <- reactive({
        create_kmplot(
          fit = all_fit(),
          df = all_survival(),
          confint = TRUE,
          risktable = TRUE,
          title = test_ds(),
          group_colors = c("red", "green"),
          facet = TRUE)
      })

      #the KM Plots are stored as a list, so a few adjustments are necessary to plot everything
      shiny::observe({
        output$km_plots <- renderUI({
          plot_output_list <-
            lapply(1:length(all_survival()), function(i) {
              plotname <- names(all_survival())[i]
              plotOutput(ns(plotname), height = 600)
            })
          do.call(tagList, plot_output_list)
        })
      })

      shiny::observe({
        lapply(1:length(test_ds()), function(i){
          my_dataset <- names(all_survival())[i]
          output[[my_dataset]] <- shiny::renderPlot({
            shiny::req(prediction_test())
            all_kmplot()[i]
          })
        })
      })

    }
  )
}
