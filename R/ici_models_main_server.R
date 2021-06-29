ici_models_main_server <- function(
  id,
  variables_list
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$bucket_list <- shiny::renderUI({
        list_format <- "<p style = 'color:Gray; font-size: 12px; height: 18px;'>"
        sortable::bucket_list(
          header = "1. Select training and testing datasets",
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

      observe(shiny::updateSelectizeInput(session, 'predictors_clinical_data',
                                          choices = variables_list$clinical_data,
                                          selected = NULL,
                                          server = TRUE))
      observe(shiny::updateSelectizeInput(session, 'predictors_immunefeatures',
                                          choices = variables_list$immunefeatures,
                                          selected = NULL,
                                          server = TRUE))
      observe(shiny::updateSelectizeInput(session, 'predictors_biomarkers',
                                          choices = variables_list$biomarkers,
                                          selected = NULL,
                                          server = TRUE))
      observe(shiny::updateSelectizeInput(session, 'predictors_gene',
                                          choices = variables_list$genes,
                                          selected = NULL,
                                          server = TRUE))

      predictors <- shiny::reactive(
        c(input$predictors_clinical_data, input$predictors_immunefeatures, input$predictors_biomarkers, input$predictors_gene)
      )

      output$categoric_pred <- shiny::renderUI({
        shiny::req(input$balance_pred == TRUE)
        selected_pred <- training_obj()$predictors %>% filter(VariableType == "Categorical") %>% pull(feature_name, name = feature_display)
        shiny::validate(shiny::need(length(selected_pred)>0, "No categorical predictor was selected."))
        shiny::checkboxGroupInput(
          ns("pred_to_balance"),
          label = "Select variable(s) to be balanced",
          choices = selected_pred
        )
      })
      output$num_transform <- shiny::renderUI({
        selected_pred <- training_obj()$predictors %>% filter(VariableType == "Numeric") %>% pull(feature_name, name = feature_display)
        shiny::validate(shiny::need(length(selected_pred)>0, "No numeric predictor was selected."))
        shiny::checkboxGroupInput(
          ns("pred_to_transform"),
          label = "Select variable(s) to scale",
          choices = selected_pred
        )
      })

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
          feature_df = ioresponse_data$feature_df,
          previous_treat_to_exclude = input$exclude_previous
        )
      })

      output$samples_summary <- shiny::renderText({
        shiny::req(training_obj())
        paste("Samples in training set:", nrow(training_obj()$subset_df$train_df),
              "Samples in testing set:", nrow(training_obj()$subset_df$test_df))
      })

      output$train_summary <- shiny::renderText({
        shiny::req(training_obj())
        paste0("Selected formula: Response to ICI ~ ",
               paste(subset(training_obj()$predictors, VariableType != "Category")$feature_display, collapse = " + ")
               )
      })
#TODO: test checks for NAs and missing levels
      observe({ #block Train button if one of the datasets is missing annotation for one predictor. Notify number of samples with NA that will be excluded
        shiny::req(training_obj())

        if(dplyr::n_distinct(training_obj()$subset_df$train_df$Study) >1) shiny::showNotification("Warning: You selected datasets with samples from different tumor types. The data from these datasets will be merged for training.", duration = 10, id = "mix_types")
        else shiny::removeNotification(id = "mix_types")

        if((nrow(training_obj()$subset_df$train_df)/length(predictors()))<10) shiny::showNotification("Warning: The number of selected predictors is higher than 10% of the number of samples selected for training.", duration = 10, id = "high_pred")
        else shiny::removeNotification(id = "high_pred")

        if(nrow(training_obj()$missing_annot) == 0 & length(training_obj()$missing_level)==0){
          shinyjs::enable("compute_train")
          shinyjs::hide("missing_data")
          shinyjs::hide("missing_sample")
         }else{
          if(nrow(training_obj()$missing_annot)>0){ #checks for missing data
            if(any(training_obj()$missing_annot$missing_all == 1)){ #dataset doesn't have annotation for one selected feature
              shinyjs::disable("compute_train")
              shinyjs::show("missing_data")
              output$missing_data <- shiny::renderText({
                shiny::req(nrow(training_obj()$missing_annot) != 0)
                missing_all <- (training_obj()$missing_annot %>% dplyr::filter(missing_all == 1))
                paste("<ul><i> Change the following dataset and/or predictor selection to proceed:</i><br>",
                      paste0("<li>Dataset ", missing_all$dataset, " has no data for ", missing_all$feature_display,".", collapse = "</li>"), "</ul>")
                })
            }else{
              shinyjs::enable("compute_train")
              shinyjs::hide("missing_data")
            }

            if(any(training_obj()$missing_annot$missing_all == 0)){ #samples with missing info for a selected feature
              shinyjs::show("missing_sample")
              output$missing_sample <- shiny::renderText({
                shiny::req(nrow(training_obj()$missing_annot) != 0)
                missing_some <- (training_obj()$missing_annot %>% dplyr::filter(missing_all == 0))
                paste("<i> Samples with NA annotation will be excluded from modeling:</i><br>",
                      paste0(missing_some$dataset, " has ", missing_some$n_missing," samples with NA info for ", missing_some$feature_display, ".", collapse = "<br>"))
              })
            }
          } #ends check for NAs in annotation
         if(length(training_obj()$missing_level)>0){
           shinyjs::disable("compute_train")
           output$missing_level <- shiny::renderText({
             shiny::req(nrow(training_obj()$missing_level) != 0)
             paste("<ul><i> Categorical level only present in testing dataset - change the dataset and/or predictor selection to proceed:</i><br>",
                   paste0("<li>", training_obj()$missing_level$feature_display, " has group ", training_obj()$missing_level$group, " only at the testing set (",
                          training_obj()$missing_level$dataset, ").", collapse = "</li>"), "</ul>")
             })
         }
        }
      })

      #scale numeric variables
      selected_df <- reactive({
        list(
          train = get_scaled_data(training_obj()$subset_df$train_df, scale_function_choice = input$scale_method, predictors_to_scale = input$pred_to_transform),
          test = get_scaled_data(training_obj()$subset_df$test_df, scale_function_choice = input$scale_method, predictors_to_scale = input$pred_to_transform)
        )
      })

      train_df <- reactive({
        if(input$do_norm == TRUE){
          normalize_dataset(
            train_df = selected_df()$train,
            test_df = selected_df()$test,
            variable_to_norm = c(input$predictors_gene,
                                 input$predictors_immunefeatures,
                                 df_to_model()[input$predictors_biomarkers] %>% dplyr::select(where(is.numeric)) %>% colnames()),
            predictors = predictors(),
            is_test = FALSE)
        }else{
          scaled_df %>%
            tidyr::drop_na(any_of(predictors())) %>%
            dplyr::select("Sample_ID", "Dataset", "Responder", all_of(predictors()))
        }
      })

      test_df <- reactive({
        if(input$do_norm == TRUE){
          normalize_dataset(
            train_df = selected_df()$train,
            test_df = selected_df()$test,
            variable_to_norm = c(input$predictors_gene,
                                 input$predictors_immunefeatures,
                                 df_to_model()[input$predictors_biomarkers] %>% dplyr::select(where(is.numeric)) %>% colnames()),
            predictors = predictors(),
            is_test = TRUE)
        }else{
          scaled_df %>%
            tidyr::drop_na(any_of(predictors())) %>%
            dplyr::select("Sample_ID", "Dataset", "Responder", all_of(predictors()))
        }
      })

      advanced_selection <- shiny::reactive({
        list(
          set_seed = input$seed_value,
          balance_lhs = input$balance_resp,
          balance_rhs = input$balance_pred,
          predictors_to_balance = input$pred_to_balance
        )
      })

      ici_models_train_server(
        "ici_model1",
        shiny::reactive(training_obj()),
        train_df = shiny::reactive(train_df()),
        test_df = shiny::reactive(test_df()),
        advanced_options = shiny::reactive(advanced_selection())
        )

      ici_models_train_server(
        "ici_model2",
        shiny::reactive(training_obj()),
        train_df = shiny::reactive(train_df()),
        test_df = shiny::reactive(test_df()),
        advanced_options = shiny::reactive(advanced_selection())
      )
    }
  )
}
