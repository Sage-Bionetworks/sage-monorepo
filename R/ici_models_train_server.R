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
              lapply(names(datasets_options_train), function(x) paste(list_format, x, "</p>")),
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

      clinical_data_choices <- shiny::reactive({
        ioresponse_data$feature_df %>%
          dplyr::filter(VariableType == "Numeric" & `Variable Class` == "Clinical data") %>%
          dplyr::pull("FeatureMatrixLabelTSV")
      })

      immunefeatures_choices <-  shiny::reactive({
        ioresponse_data$feature_df %>%
          dplyr::filter(VariableType == "Numeric" & `Variable Class` != "NA") %>%
          dplyr::select(
            INTERNAL = FeatureMatrixLabelTSV,
            DISPLAY = FriendlyLabel,
            CLASS = `Variable Class`) %>% iatlas.app::create_nested_list_by_class()
      })

      biomarkers_choices <- shiny::reactive({
        ioresponse_data$feature_df %>%
          dplyr::filter(VariableType == "Numeric" & `Variable Class` == "Predictor - Immune Checkpoint Treatment") %>%
          dplyr::select(
            INTERNAL = FeatureMatrixLabelTSV,
            DISPLAY = FriendlyLabel,
            CLASS = `Variable Class`) %>% iatlas.app::create_nested_list_by_class()
      })

      gene_choices <- shiny::reactive({
        features <- iatlas.api.client::query_immunomodulators() %>%
            dplyr::select(
              "feature_name" = "entrez",
              "feature_display" = "hgnc",
              "Gene Family" = "gene_family",
              "Gene Function" = "gene_function",
              "Immune Checkpoint" = "immune_checkpoint",
              "Super Category" = "super_category"
            )

          features %>%
            filter(feature_display %in% colnames(ioresponse_data$im_expr)) %>%
            mutate(INTERNAL = feature_display) %>%
            dplyr::select(
              INTERNAL,
              DISPLAY = feature_display,
              CLASS = `Gene Family`
            ) %>% iatlas.app::create_nested_list_by_class()
      })

      observe(shiny::updateSelectizeInput(session, 'predictors_clinical_data',
                                          choices = clinical_data_choices(),
                                          selected = NULL,
                                          server = TRUE))

      observe(shiny::updateSelectizeInput(session, 'predictors_immunefeatures',
                                  choices = immunefeatures_choices(),
                                  selected = NULL,
                                  server = TRUE))
      observe(shiny::updateSelectizeInput(session, 'predictors_biomarkers',
                                          choices = biomarkers_choices(),
                                          selected = NULL,
                                          server = TRUE))

      observe(shiny::updateSelectizeInput(session, 'predictors_gene',
                                          choices = gene_choices(),
                                          selected = NULL,
                                          server = TRUE))

      predictors <- shiny::reactive(
        c(input$predictors_clinical_data, input$predictors_immunefeatures, input$predictors_biomarkers, input$predictors_gene)
      )

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
        shiny::req(predictors())
        paste0("Selected formula: Response to ICI ~ ", paste(predictors(), collapse = " + "))
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
          variable_to_norm = input$predictors_gene,
          predictors = predictors(),
          is_test = FALSE
        )
      })

      test_df <- eventReactive(input$compute_test,{
        shiny::req(model_train())
        normalize_dataset(
          df = df_to_model(),
          train_ds = train_ds(),
          test_ds = test_ds(),
          variable_to_norm = input$predictors_gene,
          predictors = predictors(),
          is_test = TRUE
        )
      })

      #Running model
      model_train <- eventReactive(input$compute_train, {
        shiny::validate(shiny::need(length(predictors())>1, "Select predictors for model training."))
        if(!is.na(input$seed_value)) set.seed(input$seed_value)
        run_elastic_net(
          train_df = train_df(),
          response_variable = "Responder",
          predictors = predictors(),
          n_cv_folds = input$cv_number
        )
      })

      output$results <- DT::renderDataTable({
        shiny::req(model_train())
        numeric_columns <- colnames(model_train()$results[1, sapply(model_train()$results,is.numeric)])
        DT::datatable(
          model_train()$results[rownames(model_train()$bestTune),],
          rownames = FALSE,
          options = list(dom = 't')
        ) %>% DT::formatRound(columns =numeric_columns, digits = 3)
      })

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

      ###TEST

      prediction_test <- eventReactive(input$compute_test, {
        iatlas.app::get_testing_data(model_train(), test_df(), test_datasets = test_ds(), survival_data = df_to_model())
      })

      output$confusion_matrix <- DT::renderDataTable({
        shiny::req(prediction_test())
        cm <- table(test_df()$Responder, prediction_test()$predictions) %>%
          as.data.frame() %>%
          tidyr::pivot_wider(names_from = Var2, names_prefix = "Predicted ",values_from = Freq)
        colnames(cm)[1] <- " "

        DT::datatable(
          cm,
          rownames = FALSE,
          options = list(dom = 't')
        ) %>%
          DT::formatStyle(" ", fontWeight = 'bold')
      })

      output$accuracy <- renderPrint({
        shiny::req(prediction_test())
        prediction_test()$accuracy_results
      })

      output$roc <- renderPlot({
        shiny::req(prediction_test())
        plot(prediction_test()$roc_plot, print.auc = TRUE)
      })

      #code for km plot

      #the KM Plots are stored as a list, so a few adjustments are necessary to plot everything
      shiny::observeEvent(input$compute_test,{
        shiny::req(prediction_test())
        output$km_plots <- renderUI({
          plot_output_list <-
            lapply(1:length(prediction_test()$km_plots), function(i) {
              plotname <- names(prediction_test()$km_plots)[i]
              plotOutput(ns(plotname), height = 600)
            })
          do.call(tagList, plot_output_list)
        })
      })

      shiny::observeEvent(input$compute_test,{
        lapply(1:length(shiny::isolate(test_ds())), function(i){
          my_dataset <- names(prediction_test()$km_plots)[i]
          output[[my_dataset]] <- shiny::renderPlot({
            shiny::req(prediction_test())
            prediction_test()$km_plots[i]
          })
        })
      })

      observeEvent(input$compute_train,{
        shinyjs::hide("confusion_matrix")
        shinyjs::hide("accuracy")
        shinyjs::hide("roc")
        shinyjs::hide("km_plots")
      })
      observeEvent(input$compute_test,{
        shinyjs::show("confusion_matrix")
        shinyjs::show("accuracy")
        shinyjs::show("roc")
        shinyjs::show("km_plots")
      })
    }
  )
}
