ici_models_train_server <- function(
  id,
  training_obj,
  train_df,
  test_df,
  advanced_options,
  blocked_train
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      shiny::observe({ #we need to block the train button in case the selection will cause error in training or testing
        shinyjs::toggleState("compute_train", condition = (blocked_train() == FALSE))
      })

      #Running model
      train_method <- reactive({
        switch(
          input$train_method,
          "Elastic Net Regression" = run_elastic_net,
          "Logistic Regression" = run_logistic_reg,
          "Random Forest" = run_rf,
          "XGBoost" = run_xgboost,
        )
      })
      model_train <- eventReactive(input$compute_train, {
        shiny::validate(shiny::need(length(training_obj()$predictors)>1, "Select predictors for model training."))
        if(advanced_options()$balance_rhs == TRUE) shiny::validate(shiny::need(length(advanced_options()$predictors_to_balance)>0, "Select categorical predictors for balancing (advanced options)"))
        if(!is.na(advanced_options()$set_seed)) set.seed(advanced_options()$set_seed)

        train_method()(
          train_df = train_df(),
          response_variable = training_obj()$response_var,
          predictors = training_obj()$predictor,
          n_cv_folds = input$cv_number,
          balance_lhs = advanced_options()$balance_lhs,
          balance_rhs = advanced_options()$balance_rhs,
          predictors_to_balance = advanced_options()$predictors_to_balance
        )
      })

      output$results <- DT::renderDataTable({
        shiny::req(model_train())
        model_train()$results
      })

      output$plot_coef <- plotly::renderPlotly({
        shiny::req(model_train())
        model_train()$plot
      })

      ###TEST
      prediction_test <- eventReactive(input$compute_test, {
        iatlas.app::get_testing_results(model_train()$model,
                                        test_df(),
                                        training_obj())
      })
      #Results of test for each selected dataset are stored in a list, so below we will plot all elements in the list
      shiny::observeEvent(input$compute_test,{
          shiny::req(prediction_test())
          output$test_plots <- renderUI({
            plot_output_list <-
              lapply(1:length(training_obj()$dataset$test), function(i) {
                plotname <- training_obj()$dataset$test[i]
                shiny::verticalLayout(

                  shiny::fluidRow(
                    shiny::column(
                      width = 8,
                      plotOutput(ns(paste0("km_", plotname)), height = 300),
                    ),
                    shiny::column(
                      width = 4,
                      plotOutput(ns(paste0("roc_", plotname)), height = 200)
                    )
                  ),
                  verbatimTextOutput(ns(paste0("ac_", plotname))),
                  shiny::hr()
                )
              })
            do.call(tagList, plot_output_list)
          })
        })

        shiny::observeEvent(input$compute_test,{
          lapply(1:length(shiny::isolate(training_obj()$dataset$test)), function(i){
            my_dataset <- training_obj()$dataset$test[i]
            output[[paste0("ac_", my_dataset)]] <- shiny::renderPrint({
              shiny::req(prediction_test())
              prediction_test()[[i]]$accuracy_results
            })
            output[[paste0("roc_", my_dataset)]] <- shiny::renderPlot({
              shiny::req(prediction_test())
              prediction_test()[[i]]$roc_plot
            })
            output[[paste0("km_", my_dataset)]] <- shiny::renderPlot({
              shiny::req(prediction_test())
              prediction_test()[[i]]$km_plot
            })
          })
        })

      #if user creates a new model, previous testing results will be hidden
      shiny::observeEvent(input$compute_train,{
        shinyjs::hide("test_plots")
      })
      shiny::observeEvent(input$compute_test,{
        shinyjs::show("test_plots")
      })

      output$download_train <- shiny::downloadHandler(
        filename = function() stringr::str_c("train-", Sys.Date(), ".tsv"),
        content = function(con) readr::write_delim(dplyr::mutate(train_df(), prediction = predict(model_train()$model, newdata = train_df())), con, delim = "\t")
      )
      output$download_test <- shiny::downloadHandler(
        filename = function() stringr::str_c("test-", Sys.Date(), ".tsv"),
        content = function(con) readr::write_delim(purrr::map_dfr(prediction_test(), function(x)x$results), con, delim = "\t")
      )
    }
  )
}
