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
# Training Methods
#########################
#Elastic Net
run_elastic_net <- function(train_df, response_variable, predictors, n_cv_folds){
  print("training model")
  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors), collapse = "+")))
  caret::train(
    parameters, data = train_df, method = "glmnet",
    trControl = caret::trainControl("cv", number = n_cv_folds),
    tuneLength = 15
  )
}

#####
# Training  results
#


#testing results object


get_testing_data <- function(model, test_df, test_datasets, survival_data){
  predictions <- predict(model, newdata = test_df)

  accuracy_results <- caret::confusionMatrix(predictions, as.factor(test_df$Responder), positive = "Responder")

  roc_plot <- pROC::roc(
    response = factor(test_df$Responder,  ordered = TRUE),
    predictor = factor(predictions, ordered = TRUE)
  )

  #data for KM plot
  df_km <- merge(cbind(test_df, prediction = predictions), survival_data %>% select(Sample_ID, "OS_time", "OS"), by = "Sample_ID")

  surv_df <- purrr::map(.x = test_datasets, df = df_km, .f= function(dataset, df){
    dataset_df <- df %>%
      dplyr::filter(Dataset == dataset)

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
    predictions = predictions,
    accuracy_results = accuracy_results,
    roc_plot = roc_plot,
    km_plots = all_kmplot
  )
}

