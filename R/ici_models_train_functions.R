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

normalize_dataset <- function(df, train_ds, test_ds, variable_to_norm, predictors, is_test = FALSE){
  if(is_test == FALSE){
    avg_train <-  NULL
    sd_train <-  NULL
    filter_ds <- train_ds
  }else{
    avg_train <- colMeans((df %>% dplyr::filter(Dataset %in% train_ds))[,variable_to_norm, drop = FALSE])
    sd_train <- apply((df %>% dplyr::filter(Dataset %in% train_ds))[,variable_to_norm, drop = FALSE], 2, sd)
    filter_ds <- test_ds
  }
  df %>%
    dplyr::filter(Dataset %in% filter_ds) %>%
    dplyr::group_by(Dataset) %>%
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

get_training_object <- function(data_df, train_ds, test_ds, selected_pred, selected_genes, feature_df = ioresponse_data$feature_df){

  #df with train and test
  dataset_selection <- list(
    train = get_dataset_id(train_ds),
    test = get_dataset_id(test_ds)
  )

  #df with selected predictors and labels
  predictors <- feature_df %>%
    dplyr::filter(FeatureMatrixLabelTSV %in% selected_pred) %>%
    dplyr::select("feature_name" = FeatureMatrixLabelTSV, "feature_display" = FriendlyLabel)

  predictors <- rbind(predictors, data.frame(feature_name = selected_genes, feature_display = selected_genes))

  #subset dataset
  data_bucket <- list(
    train_df = data_df %>%
      dplyr::filter(Dataset %in% dataset_selection$train) %>%
      tidyr::drop_na(any_of(predictors$feature_name)),
    test_df = data_df %>%
      dplyr::filter(Dataset %in% dataset_selection$test) %>%
      tidyr::drop_na(any_of(predictors$feature_name))
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

  #normalize train and test, count number of samples in each
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
#Elastic Net
run_elastic_net <- function(train_df, response_variable, predictors, n_cv_folds){
  print("training model")
  cvIndex <- caret::createFolds(y = factor(train_df[[response_variable]]), k = n_cv_folds, returnTrain = TRUE)
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

  fmx_pred <- purrr::map(.x = test_datasets, function(x){
    test_df %>%
      dplyr::filter(Dataset == x) %>%
      dplyr::mutate(prediction = predict(model, newdata = .))
  })
  names(fmx_pred) <- test_datasets

  accuracy_results <- lapply(fmx_pred, function(x) caret::confusionMatrix(x$prediction, as.factor(x$Responder)))

  roc_plot <- purrr::map(fmx_pred, function(x){
    rplot <- pROC::roc(
      response = factor(x$Responder,  ordered = TRUE),
      predictor = factor(x$prediction, ordered = TRUE),
      auc = TRUE
    )
    pROC::ggroc(rplot, print.auc = TRUE)
  })


  # predictions <- predict(model, newdata = test_df)
  #
  # accuracy_results <- caret::confusionMatrix(predictions, as.factor(test_df$Responder), positive = "Responder")
  #
  # roc_plot <- pROC::roc(
  #   response = factor(test_df$Responder,  ordered = TRUE),
  #   predictor = factor(predictions, ordered = TRUE)
  # )

  #data for KM plot
  #df_km <- merge(cbind(test_df, prediction = predictions), survival_data %>% select(Sample_ID, "OS_time", "OS"), by = "Sample_ID")

  surv_df <- purrr::map(.x = fmx_pred, df = survival_data, .f= function(dataset, df){
    dataset_df <- df %>%
      select(Sample_ID, OS, OS_time, PFI_1, PFI_time_1) %>%
      merge(., dataset, by = "Sample_ID")

    build_survival_df(
      df = dataset_df,
      group_column = "prediction",
      group_options = "prediction",
      time_column = "OS_time"
    )
  })

  all_fit <- purrr::map(surv_df,
                        function(df) survival::survfit(survival::Surv(time, status) ~ variable, data = df))

  all_kmplot <- create_kmplot(fit = all_fit,
                              df = surv_df,
                              confint = TRUE,
                              risktable = TRUE,
                              title = test_datasets,
                              group_colors = c("red", "green"),
                              facet = TRUE)

  #sending all data in a list
  list(
    predictions = fmx_pred,
    accuracy_results = accuracy_results,
    roc_plot = roc_plot,
    km_plots = all_kmplot
  )
}

