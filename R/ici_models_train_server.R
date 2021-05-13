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


      df_to_model <- reactive({
        ioresponse_data$fmx_df %>%
          dplyr::filter(treatment_when_collected == "Pre") %>%
          tidyr::drop_na("Responder") %>% #excluding samples that don't have response annotation
          merge(., ioresponse_data$im_expr, by = "Sample_ID")
      })

      training_obj <- reactive({
        shiny::req(df_to_model(), input$train, input$test, predictors())
        get_training_object(
          data_df = df_to_model(),
          train_ds = input$train,
          test_ds = input$test,
          selected_pred = predictors(),
          selected_genes = input$predictors_gene,
          feature_df = ioresponse_data$feature_df
        )
      })

      output$samples_summary <- shiny::renderText({
        shiny::req(training_obj())
        paste("Samples in training set:", nrow(training_obj()$subset_df$train_df),
              "Samples in testing set:", nrow(training_obj()$subset_df$test_df))
      })

      output$train_summary <- shiny::renderText({
        shiny::req(training_obj())
        paste0("Selected formula: Response to ICI ~ ", paste(training_obj()$predictors$feature_display, collapse = " + "))
      })

      observe({ #block Train button if one of the datasets is missing annotation for one predictor. Notify number of samples with NA that will be excluded
        shiny::req(training_obj())
        if(nrow(training_obj()$missing_annot) == 0){
          shinyjs::enable("compute_train")
          shinyjs::hide("missing_data")
          shinyjs::hide("missing_sample")
        }else{
          if(any(training_obj()$missing_annot$missing_all == 1)){ #dataset doesn't have annotation for one selected feature
            shinyjs::disable("compute_train")
            shinyjs::show("missing_data")
            output$missing_data <- shiny::renderText({
              shiny::req(nrow(training_obj()$missing_annot) != 0)
              missing_all <- (training_obj()$missing_annot %>% dplyr::filter(missing_all == 1))
              paste("Dataset ", missing_all$dataset, "has no data for ", missing_all$feature,
                    ". Change the dataset and/or predictor selection to proceed.")})
          }else{
            shinyjs::enable("compute_train")
            shinyjs::hide("missing_data")
          }
          if(any(training_obj()$missing_annot$missing_all == 0)){ #samples with missing info for a selected feature
            shinyjs::show("missing_sample")
            output$missing_sample <- shiny::renderText({
              shiny::req(nrow(training_obj()$missing_annot) != 0)
              missing_some <- (training_obj()$missing_annot %>% dplyr::filter(missing_all == 0))
              paste("Dataset ", missing_some$dataset, "has ", missing_some$n_missing, " samples with NA info for ", missing_some$feature,
                    " that will be excluded from modeling.")
            })
          }else{
            shinyjs::hide("missing_sample")
          }
        }
      })

      train_df <- eventReactive(input$compute_train,{
        normalize_dataset(
          df = df_to_model(),
          train_ds = training_obj()$dataset$train,
          test_ds = training_obj()$dataset$test,
          variable_to_norm = c(input$predictors_gene, input$biomarkers_choices),
          predictors = predictors(),
          is_test = FALSE
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
          feature_name = coef(model_train()$finalModel,
                               model_train()$bestTune$lambda)@Dimnames[[1]][coef(model_train()$finalModel, model_train()$bestTune$lambda)@i+1],
          error = 0
        )

        plot_df <- merge(plot_df, training_obj()$predictors, by = "feature_name", all.x = TRUE) %>%
          dplyr::mutate(feature_display = replace(feature_display, feature_name == "(Intercept)", "(Intercept)")) %>%
          dplyr::select(x, y = feature_display, error)

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

      test_df <- eventReactive(input$compute_test,{
        normalize_dataset(
          df = df_to_model(),
          train_ds = training_obj()$dataset$train,
          test_ds = training_obj()$dataset$test,
          variable_to_norm = input$predictors_gene,
          predictors = predictors(),
          is_test = TRUE
        )
      })

      prediction_test <- eventReactive(input$compute_test, {
        iatlas.app::get_testing_results(model_train(),
                                        test_df(),
                                        test_datasets = training_obj()$dataset$test,
                                        survival_data = df_to_model())
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
                      width = 5,
                      plotOutput(ns(paste0("km_", plotname)), height = 400)
                    ),
                    shiny::column(
                      width = 3,
                      plotOutput(ns(paste0("roc_", plotname)), height = 300)
                    ),
                    shiny::column(
                      width = 4,
                      verbatimTextOutput(ns(paste0("ac_", plotname)))
                    )
                  ),
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

      shiny::observeEvent(input$compute_train,{#if user creates a new model, previous testing results will be hidden
        shinyjs::hide("test_plots")
      })
      shiny::observeEvent(input$compute_test,{
        shinyjs::show("test_plots")
      })
    }
  )
}
