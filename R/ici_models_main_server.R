ici_models_main_server <- function(
  id,
  cohort_obj,
  variables_list
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$excluded_dataset <- shiny::renderText({
        if(identical(unique(cohort_obj()$group_tbl$dataset_display), cohort_obj()$dataset_displays)){
          ""
        }else{
          excluded_datasets <- setdiff(cohort_obj()$dataset_displays, unique(cohort_obj()$group_tbl$dataset_display))
          paste(
            paste(excluded_datasets, collapse = ", "),
            " not available for training because all samples were filtered in ICI Cohort Selection."
          )
        }
      })

      output$bucket_list <- shiny::renderUI({
        list_format <- "<p style = 'color:Gray; font-size: 12px; height: 18px;'>"
        sortable::bucket_list(
          header = "1. Select training and testing datasets",
          group_name = ns("dataset_bucket"),
          orientation = "horizontal",
          sortable::add_rank_list(
            text = "Datasets available",
            labels = lapply(
              lapply(unique(cohort_obj()$group_tbl$dataset_display), function(x) paste(list_format, x, "</p>")),
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
      observe(shiny::updateSelectizeInput(session, 'response_variable',
                                          choices = variables_list$response_vars,
                                          selected = "Responder",
                                          server = TRUE))

      predictors <- shiny::reactive(
        c(input$predictors_clinical_data, input$predictors_immunefeatures, input$predictors_biomarkers, input$predictors_gene)
      )

      output$categoric_pred <- shiny::renderUI({
        shiny::req(input$balance_pred == TRUE)
        shiny::validate(shiny::need(length(input$predictors_clinical_data)>0, "No categorical predictor was selected."))
        shiny::checkboxGroupInput(
          ns("pred_to_balance"),
          label = "Select variable(s) to be balanced",
          choices = names(variables_list$clinical_data)[match(input$predictors_clinical_data,variables_list$clinical_data)]
        )
      })
      output$num_transform <- shiny::renderUI({
        selected_pred <- training_obj()$predictors %>%
          dplyr::filter(feature_name %in% c(input$predictors_immunefeatures, input$predictors_biomarkers,input$predictors_gene)) %>%
          dplyr::pull(feature_display)

        shiny::validate(shiny::need(length(selected_pred)>0, "No numeric predictor was selected."))
        shiny::checkboxGroupInput(
          ns("pred_to_transform"),
          label = "Select variable(s) to scale",
          choices = selected_pred
        )
      })

      block_train <- shiny::reactiveVal(TRUE) #Train button blocked at startup
      training_obj <- reactive({

        if( (length(input$train) == 0) | (length(input$test) == 0) | length(predictors()) < 2) block_train(TRUE)
        else block_train(FALSE)

        shiny::validate(
          shiny::need(input$train, "Select training dataset(s)"),
          shiny::need(input$test, "Select testing dataset(s)"),
          shiny::need(length(predictors())>1, "Select two or more predictors")
        )

        get_training_object(
          cohort_obj = cohort_obj(),
          train_ds = input$train,
          test_ds = input$test,
          selected_response = input$response_variable,
          selected_pred = predictors(),
          selected_genes = input$predictors_gene,
          previous_treat_to_exclude = input$exclude_previous
        )
      })

      output$samples_summary <- shiny::renderUI({
        shiny::req(training_obj())

        shiny::verticalLayout(
          shiny::p(
            paste(
              paste("Samples in training set: ", nrow(training_obj()$subset_df$train_df)),
              paste("Samples in testing set: ", nrow(training_obj()$subset_df$test_df)),
              sep = " | "
            )
          ),
          shiny::br(),
          shiny::p(
            paste0("Selected formula: ", names(variables_list$response_vars)[match(input$response_variable,variables_list$response_vars)],
                   " ~ ", paste(subset(training_obj()$predictors, VariableType != "Category")$feature_display, collapse = " + ")
            )
          )
        )
      })

      output$response_characteristics <- shiny::renderText({
        shiny::req(input$response_variable)
        iatlas.api.client::query_tags(tags = input$response_variable) %>% dplyr::pull(tag_characteristics)
      })


      observe({ #block Train button if one of the datasets is missing annotation for one predictor. Notify number of samples with NA that will be excluded
        shiny::req(training_obj())

        if(dplyr::n_distinct(training_obj()$subset_df$train_df$TCGA_Study) >1) shiny::showNotification("Warning: You selected datasets with samples from different tumor types. The data from these datasets will be merged for training.", duration = 10, id = "mix_types")
        else shiny::removeNotification(id = "mix_types")

        if((nrow(training_obj()$subset_df$train_df)/length(predictors()))<10) shiny::showNotification("Warning: The number of selected predictors is higher than 10% of the number of samples selected for training.", duration = 10, id = "high_pred")
        else shiny::removeNotification(id = "high_pred")

        if(nrow(training_obj()$missing_annot) == 0 & length(training_obj()$missing_level)==0){
          block_train(FALSE)
          shinyjs::hide("missing_data")
          shinyjs::hide("missing_sample")
         }else{
          if(nrow(training_obj()$missing_annot)>0){ #checks for missing data
            if(any(c("feature_all_na" ,"tag_all_na") %in% (training_obj()$missing_annot$missing_all))){#dataset doesn't have annotation for one selected feature
              block_train(TRUE)
              shinyjs::show("missing_data")
              output$missing_data <- shiny::renderText({
                shiny::req(nrow(training_obj()$missing_annot) != 0)
                missing_all <- (training_obj()$missing_annot %>% dplyr::filter(missing_all %in% c("feature_all_na" ,"tag_all_na")))
                paste("<ul><i> Change the following dataset and/or predictor selection to proceed:</i><br>",
                      paste0("<li>Dataset ", missing_all$dataset, " has no data for ", missing_all$feature_display,".", collapse = "</li>"), "</ul>")
                })
            }else{
              shinyjs::hide("missing_data")
            }

            if("tag_one_level" %in% training_obj()$missing_annot$missing_all){
              block_train(TRUE)
              shinyjs::show("single_level")
              output$single_level <- shiny::renderText({
                shiny::req(nrow(training_obj()$missing_annot) != 0)
                missing_all <- (training_obj()$missing_annot %>% dplyr::filter(missing_all == "tag_one_level"))
                paste("<ul><i> Change the following dataset and/or predictor selection to proceed:</i><br>",
                      paste0("<li>Dataset ", missing_all$dataset, " has only one level for ", missing_all$feature_display,".", collapse = "</li>"), "</ul>")
              })
            }else{
              shinyjs::hide("single_level")
            }

            if(!any(c("feature_all_na" ,"tag_all_na", "tag_one_level") %in% (training_obj()$missing_annot$missing_all))){
              block_train(FALSE)
            }

            if(any(c("feature_some_na" ,"tag_some_na") %in% training_obj()$missing_annot$missing_all)){ #samples with missing info for a selected feature
              shinyjs::show("missing_sample")

              output$missing_sample <- shiny::renderText({
                shiny::req(nrow(training_obj()$missing_annot) != 0)
                missing_some <- (training_obj()$missing_annot %>% dplyr::filter(missing_all %in% c("feature_some_na" ,"tag_some_na")))
                paste("<ul><i> Samples with NA annotation will be excluded from modeling:</i><br>",
                      paste0("<li>", missing_some$dataset, " has ", missing_some$n_missing," samples with NA info for ", missing_some$feature_display, ".", collapse = "</li>"), "</ul>")
              })
            }
          } #ends check for NAs in annotation
         if(length(training_obj()$missing_level)>0){
           block_train(TRUE)
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
            variable_to_norm = dplyr::filter(training_obj()$predictors, VariableType == "Numeric")$feature_name,
            predictors = c(input$response_variable, dplyr::filter(training_obj()$predictors, VariableType != "Category")$feature_name),
            is_test = FALSE) %>%
          dplyr::filter(dplyr::across(tidyselect::everything(), ~ !stringr::str_starts(., "na_")))
        }else{
          selected_df()$train %>%
            tidyr::drop_na(any_of(predictors())) %>%
            dplyr::filter(dplyr::across(tidyselect::everything(), ~ !stringr::str_starts(., "na_"))) %>%
            dplyr::select("sample_name", "dataset_name", input$response_variable, all_of(dplyr::filter(training_obj()$predictors, VariableType != "Category")$feature_name))
        }
      })

      test_df <- reactive({
        if(input$do_norm == TRUE){
          normalize_dataset(
            train_df = selected_df()$train,
            test_df = selected_df()$test,
            variable_to_norm = dplyr::filter(training_obj()$predictors, VariableType == "Numeric")$feature_name,
            predictors = c(input$response_variable, dplyr::filter(training_obj()$predictors, VariableType != "Category")$feature_name),
            is_test = TRUE) %>%
          dplyr::filter(dplyr::across(tidyselect::everything(), ~ !stringr::str_starts(., "na_")))
        }else{
          selected_df()$test %>%
            tidyr::drop_na(any_of(predictors())) %>%
            dplyr::filter(dplyr::across(tidyselect::everything(), ~ !stringr::str_starts(., "na_"))) %>%
            dplyr::select("sample_name", "dataset_name", input$response_variable, all_of(dplyr::filter(training_obj()$predictors, VariableType != "Category")$feature_name))
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

      observeEvent(input$method_link,{
        shiny::showModal(modalDialog(
          title = "Method",
          includeMarkdown("inst/markdown/methods/ici_models.markdown"),
          easyClose = TRUE,
          footer = NULL
        ))
      })

      ici_models_train_server(
        "ici_model1",
        shiny::reactive(training_obj()),
        train_df = shiny::reactive(train_df()),
        test_df = shiny::reactive(test_df()),
        advanced_options = shiny::reactive(advanced_selection()),
        blocked_train = shiny::reactive(block_train())
        )

      ici_models_train_server(
        "ici_model2",
        shiny::reactive(training_obj()),
        train_df = shiny::reactive(train_df()),
        test_df = shiny::reactive(test_df()),
        advanced_options = shiny::reactive(advanced_selection()),
        blocked_train = shiny::reactive(block_train())
      )
    }
  )
}
