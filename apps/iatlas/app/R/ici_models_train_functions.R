get_dataset_id <- function(ds_labels){
  sapply(ds_labels, function(x) datasets_options_train[[x]])
}

######################
# Dataset preparation
######################
normalize_variable <- function(x, is_test = FALSE, train_avg = NULL, train_sd = NULL){
  if(is_test == FALSE){
    if(min(x) == 0 & max(x) == 0) return(NA_integer_) #flag in case all values are 0 for a given dataset.
    else (x - mean(x))/sd(x)
  }else{
    (x - train_avg)/train_sd
  }
}

normalize_dataset <- function(train_df, test_df = NULL, variable_to_norm, predictors, is_test = FALSE){
  if(is_test == FALSE){
    avg_train <-  NULL
    sd_train <-  NULL
    df <- train_df
  }else{
    avg_train <- colMeans(train_df[,variable_to_norm, drop = FALSE])
    sd_train <- apply(train_df[,variable_to_norm, drop = FALSE], 2, sd)
    df <- test_df
  }
  df %>%
    dplyr::group_by(Dataset) %>%
    tidyr::drop_na(any_of(predictors)) %>%
    dplyr::mutate(across(all_of(variable_to_norm),
                         ~normalize_variable(
                           .x,
                           is_test = is_test,
                           train_avg = avg_train[dplyr::cur_column()],
                           train_sd = sd_train[dplyr::cur_column()]))) %>%
    dplyr::ungroup() %>%
    dplyr::select("Sample_ID", "Dataset", "Responder", all_of(predictors))
}

# #in case a dataset has all gene measurements = 0, exclude it
# fmx_train <- fmx_train[, colSums(is.na(fmx_train)) == 0]


#########################
# Training  parameters
#########################
get_scaled_data <- function(df, scale_function_choice = "None", predictors_to_scale){

  scale_function <- switch(
    scale_function_choice,
    "None" = identity,
    "Log2" = log2,
    "Log2 + 1" = function(x) log2(x + 1),
    "Log10" = log10,
    "Log10 + 1" = function(x) log10(x + 1)
  )
  df %>%
    mutate(across(all_of(predictors_to_scale), scale_function))
}

exclude_treatment_data <- function(x){
  is.na(x) | x == FALSE
}
get_training_object <- function(data_df, train_ds, test_ds,
                                selected_pred, selected_genes,
                                feature_df = ioresponse_data$feature_df,
                                scale_function_choice = "None", predictors_to_scale,
                                previous_treat_to_exclude = NULL){

  #df with train and test
  dataset_selection <- list(
    train = get_dataset_id(train_ds),
    test = get_dataset_id(test_ds)
  )

  #df with selected predictors and labels
  pred_features <- feature_df %>%
    dplyr::filter(FeatureMatrixLabelTSV %in% selected_pred) %>%
    dplyr::select("feature_name" = FeatureMatrixLabelTSV, "feature_display" = FriendlyLabel, VariableType)

  #for categorical predictors, we need to make sure to store the key to label them correctly
  categories <- ioresponse_data$sample_group_df %>%
    dplyr::filter(Category %in% pred_features$feature_name) %>%
    dplyr::mutate(feature_name = paste0(Category, FeatureValue),
                  feature_display = FeatureLabel,
                  VariableType = "Category") %>%
    dplyr::select(feature_name, feature_display, VariableType)

  genes <- data.frame()
  if(length(selected_genes >0)) genes <- data.frame(
                                            feature_name = selected_genes,
                                            feature_display = selected_genes,
                                            VariableType = "Numeric")
  predictors <- rbind(pred_features, categories, genes)

  #subset dataset
  data_bucket <- list(
    train_df = data_df %>%
      dplyr::filter(Dataset %in% dataset_selection$train) %>%
      tidyr::drop_na(dplyr::any_of(predictors$feature_name)) %>%
      dplyr::filter(dplyr::if_all(previous_treat_to_exclude, exclude_treatment_data)),
    test_df = data_df %>%
      dplyr::filter(Dataset %in% dataset_selection$test) %>%
      tidyr::drop_na(dplyr::any_of(predictors$feature_name)) %>%
      dplyr::filter(dplyr::if_all(previous_treat_to_exclude, exclude_treatment_data))
  )
  #Check if any of the selected predictors is missing for a specific dataset (eg, IMVigor210 doesn't have Age data)
  missing_annot <- purrr::map_dfr(.x = c(dataset_selection$train, dataset_selection$test), predictor = selected_pred, fmx_df = data_df, function(dataset, predictor, fmx_df){
    feature <- sapply(data_df %>% dplyr::filter(Dataset == dataset) %>% dplyr::select(predictor), function(x)sum(is.na(x)))

    if(length(feature[feature != 0])>0){
      n_samples <- nrow(data_df[data_df$Dataset == dataset,])
      data.frame(feature = names(feature[feature != 0]),
                 dataset = dataset,
                 n_missing = feature[feature != 0]) %>%
        dplyr::mutate(missing_all = dplyr::case_when(
          n_missing == n_samples ~ 1,
          TRUE ~ 0
        ))
    }
  })

  #In case a categorical predictor is selected, check if the testing dataset has the same levels included for training
  cat_predictors <-subset(predictors,VariableType == "Categorical", feature_name)
  #if some category is not annotated, we need to exclude it from training
  cat_missing <- if(nrow(cat_predictors) >0){
    purrr::map_dfr(.x = cat_predictors$feature_name, function(x){
      missing_train <- unique(data_bucket$train_df[[x]])
      missing_test <- unique(data_bucket$test_df[[x]])
      if(length(missing_test)== 0 |length(missing_train)== 0) return(NULL)

      missing_level <- setdiff(unique(data_bucket$test_df[[x]]), unique(data_bucket$train_df[[x]]))
      if(length(missing_level) != 0){
        missing_df <- data_bucket$test_df %>%
          dplyr::filter(.[[x]] %in% missing_level) %>%
          dplyr::group_by(Dataset) %>%
          dplyr::select(Dataset, dplyr::any_of(x)) %>%
          dplyr::distinct()

        cat_missing <- data.frame(
          feature_name = x,
          feature_display = subset(predictors, feature_name == x, feature_display),
          group = missing_df[[x]],
          dataset = missing_df$Dataset
        )
      }
    })
  }
  list(
   dataset = dataset_selection,
   predictors = predictors,
   subset_df = data_bucket,
   missing_annot = missing_annot,
   missing_level = cat_missing
  )
}


#########################
# Training Methods
#########################
#get folds for cross-validation
get_balance_class <- function(df, response_variable, predictors_to_balance){
    dplyr::mutate(df, balance = paste0(df[[response_variable]], df[[predictors_to_balance]]))
}
get_cv_folds <- function(train_df, balance_lhs = TRUE, balance_rhs = FALSE, response_variable, predictors_to_balance, n_cv_folds){
  if(balance_rhs == FALSE){
    if(balance_lhs == TRUE) caret::createFolds(y = factor(train_df[[response_variable]]), k = n_cv_folds, returnTrain = TRUE)
    if(balance_lhs == FALSE) return(NULL)
  }else{
    if(balance_lhs == TRUE) bdf <- get_balance_class(train_df, response_variable, predictors_to_balance)
    if(balance_lhs == FALSE) bdf <- get_balance_class(train_df, response_variable = "", predictors_to_balance)
    caret::createFolds(y = factor(bdf$balance), k = n_cv_folds, returnTrain = TRUE)
  }
}
#build table with results of cross-validation
get_table_cv_results <- function(model, has_bestTune = TRUE){
  if(has_bestTune == TRUE){
    results <-  model$results[rownames(model$bestTune),]
  }else{
    results <-  model$results
  }
  numeric_columns <- colnames(results[1, sapply(results,is.numeric)])

  DT::datatable(
    results,
    rownames = FALSE,
    options = list(dom = 't')
  ) %>% DT::formatRound(columns =numeric_columns, digits = 3)
}

#build plots with coefficients
get_plot_var_importance <- function(model, labels = NULL, from_varImp = TRUE, scale_values = FALSE, title = ""){
  if(from_varImp == TRUE){
    plot_df <- (caret::varImp(model, scale = scale_values))$importance
    plot_df$feature_name = rownames(plot_df)
    colnames(plot_df) <- c("x", "feature_name")
    plot_df$error <- 0
  }else{ #right now, only logistic regression
    plot_df <- data.frame(
      x = coef(summary(model))[,1],
      feature_name = rownames(coef(summary(model))),
      error = coef(summary(model))[,2]
    )
  }
  plot_df <- merge(plot_df, labels, by = "feature_name", all.x = TRUE) %>%
    dplyr::mutate(feature_display = replace(feature_display, feature_name == "(Intercept)", "(Intercept)")) %>%
    dplyr::select(x, y = feature_display, error)

  plot_levels <-levels(reorder(plot_df[["y"]], plot_df[["x"]], sort))

  create_barplot_horizontal(
    df = plot_df,
    x_col = "x",
    y_col = "y",
    error_col = "error",
    key_col = NA,
    color_col = "y",
    label_col = NA,
    order_by = plot_levels,
    xlab = "",
    ylab = "",
    title = title,
    showLegend = FALSE,
    source_name = NULL,
    bar_colors = "#59a0af"
  )
}

# Methods calls
run_elastic_net <- function(train_df, response_variable, predictors, labels, n_cv_folds, balance_lhs = TRUE, balance_rhs = FALSE, predictors_to_balance = NULL){
  print("training model")
  cvIndex <- get_cv_folds(train_df, balance_lhs, balance_rhs, response_variable, predictors_to_balance, n_cv_folds)

  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors), collapse = "+")))
  model <- caret::train(
    parameters, data = train_df, method = "glmnet",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = 15
  )

  results <- get_table_cv_results(model, has_bestTune = TRUE)

  plot <- get_plot_var_importance(model, labels, from_varImp = TRUE, scale_values = FALSE)

  list(model = model,
       results = results,
       plot = plot)
}

run_logistic_reg<- function(train_df, response_variable, predictors, labels, n_cv_folds, balance_lhs = TRUE, balance_rhs = FALSE, predictors_to_balance = NULL){
  print("training model")
  cvIndex <- get_cv_folds(train_df, balance_lhs, balance_rhs, response_variable, predictors_to_balance, n_cv_folds)

  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors), collapse = "+")))
  model <- caret::train(
    parameters, data = train_df, method = "glm", family = "binomial",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = 15
  )

  results <- get_table_cv_results(model, has_bestTune = FALSE)

  plot <- get_plot_var_importance(model, labels, from_varImp = FALSE)

  list(model = model,
       results = results,
       plot = plot)
}

run_xgboost <- function(train_df, response_variable, predictors, labels, n_cv_folds, balance_lhs = TRUE, balance_rhs = FALSE, predictors_to_balance = NULL){
  print("training xgboost")
  cvIndex <- get_cv_folds(train_df, balance_lhs, balance_rhs, response_variable, predictors_to_balance, n_cv_folds)

  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors), collapse = "+")))
  model <- caret::train(
    parameters, data = train_df, method = "xgbTree",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = 5
  )

  results <- get_table_cv_results(model, has_bestTune = TRUE)

  plot <- get_plot_var_importance(model, labels, from_varImp = TRUE, scale_values = TRUE)

  list(model = model,
       results = results,
       plot = plot)
}

run_rf <- function(train_df, response_variable, predictors, labels, n_cv_folds, balance_lhs = TRUE, balance_rhs = FALSE, predictors_to_balance = NULL){
  print("training random forest")
  cvIndex <- get_cv_folds(train_df, balance_lhs, balance_rhs, response_variable, predictors_to_balance, n_cv_folds)

  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors), collapse = "+")))
  model <- caret::train(
    parameters, data = train_df, method = "rf",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = length(predictors)
  )

  results <- get_table_cv_results(model, has_bestTune = TRUE)

  plot <- get_plot_var_importance(model, labels, from_varImp = TRUE, scale_values = TRUE)

  list(model = model,
       results = results,
       plot = plot)
}
#########################
# Testing Results
#########################

get_testing_results <- function(model, test_df, test_datasets, survival_data){
  purrr::map(.x = test_datasets, function(x){
    df <- test_df %>%
            dplyr::filter(Dataset == x) %>%
            dplyr::mutate(prediction = predict(model, newdata = .))

    accuracy_results <- caret::confusionMatrix(df$prediction, as.factor(df$Responder), positive = "Responder")

    rocp <- pROC::roc(
      response = factor(df$Responder,  ordered = TRUE),
      predictor = factor(df$prediction, ordered = TRUE),
      levels = c("Responder", "Non-Responder"),
      quiet = TRUE,
      auc = TRUE)

    rplot <- pROC::ggroc(rocp) + ggplot2::labs(title = paste("AUC: ", round(rocp$auc, 3)))
    #KM plot
    dataset_df <- survival_data %>%
        select(Sample_ID, OS, OS_time, PFI_1, PFI_time_1) %>%
        merge(., df, by = "Sample_ID")

    surv_df <- build_survival_df(
                      df = dataset_df,
                      group_column = "prediction",
                      group_options = "prediction",
                      time_column = "OS_time")

    fit_df <- survival::survfit(survival::Surv(time, status) ~ variable, data = surv_df)

    kmplot <- create_kmplot(fit = fit_df,
                            df = surv_df,
                            confint = TRUE,
                            risktable = FALSE,
                            title = x,
                            group_colors = c("red", "green"),
                            show_pval = TRUE,
                            show_pval_method = TRUE,
                            facet = FALSE)
    list(
        results = as.data.frame(df),
        accuracy_results = accuracy_results,
        roc_plot = rplot,
        km_plot = kmplot
    )
  })
}

