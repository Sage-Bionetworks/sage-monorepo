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

get_training_object <- function(data_df, train_ds, test_ds, selected_pred, selected_genes, feature_df = ioresponse_data$feature_df, scale_function_choice = "None", predictors_to_scale){

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
      tidyr::drop_na(dplyr::any_of(predictors$feature_name)),
    test_df = data_df %>%
      dplyr::filter(Dataset %in% dataset_selection$test) %>%
      tidyr::drop_na(dplyr::any_of(predictors$feature_name))
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

  list(
   dataset = dataset_selection,
   predictors = predictors,
   subset_df = data_bucket,
   missing_annot = missing_annot
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
#Elastic Net
run_elastic_net <- function(train_df, response_variable, predictors, n_cv_folds, balance_lhs = TRUE, balance_rhs = FALSE, predictors_to_balance = NULL){
  print("training model")
  cvIndex <- get_cv_folds(train_df, balance_lhs, balance_rhs, response_variable, predictors_to_balance, n_cv_folds)

  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors), collapse = "+")))
  caret::train(
    parameters, data = train_df, method = "glmnet",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = 15
  )
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

