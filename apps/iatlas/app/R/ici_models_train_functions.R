get_dataset_id <- function(ds_labels){
  sapply(ds_labels, function(x) datasets_options_train[[x]])
}

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
    avg_train <- colMeans((df %>% dplyr::filter(Dataset %in% train_ds))[,variable_to_norm])
    sd_train <- apply((df %>% dplyr::filter(Dataset %in% train_ds))[,variable_to_norm], 2, sd)
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
# Elastic Net Regression
#########################
run_elastic_net <- function(train_df, response_variable, predictors, n_cv_folds){
  print("training model")
  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors), collapse = "+")))
  caret::train(
    parameters, data = train_df, method = "glmnet",
    trControl = caret::trainControl("cv", number = n_cv_folds),
    tuneLength = 15
  )
}
