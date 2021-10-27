get_dataset_id <- function(ds_labels){
  sapply(ds_labels, function(x) datasets_options_train[[x]])
}
######################
# Dataset preparation
######################
normalize_variable <- function(x, is_test = FALSE, train_avg = NULL, train_sd = NULL){
  x <- as.numeric(x)
  if(is_test == FALSE){
    if(min(x) == 0 & max(x) == 0) return(NA_integer_) #flag in case all values are 0 for a given dataset.
    else (x - mean(x))/sd(x)
  }else{
    (x - train_avg)/train_sd
  }
}

normalize_dataset <- function(train_df, test_df = NULL, variable_to_norm, predictors, is_test = FALSE){
  train_df[, variable_to_norm] <- sapply(train_df[, variable_to_norm], as.numeric)
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
    dplyr::group_by(dataset_name) %>%
    tidyr::drop_na(dplyr::any_of(predictors)) %>%
    dplyr::mutate(dplyr::across(dplyr::all_of(variable_to_norm),
                         ~normalize_variable(
                           .x,
                           is_test = is_test,
                           train_avg = avg_train[dplyr::cur_column()],
                           train_sd = sd_train[dplyr::cur_column()]))) %>%
    dplyr::ungroup() %>%
    dplyr::select("sample_name", "dataset_name", "Responder", dplyr::all_of(predictors))
}

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
    dplyr::mutate(across(all_of(predictors_to_scale), scale_function))
}

exclude_treatment_data <- function(x){
  is.na(x) | x == FALSE
}
get_training_object <- function(cohort_obj,
                                train_ds,
                                test_ds,
                                selected_pred,
                                selected_genes,
                                scale_function_choice = "None", predictors_to_scale,
                                previous_treat_to_exclude = NULL){
  #df with train and test
  dataset_selection <- list(
    train = get_dataset_id(train_ds),
    test = get_dataset_id(test_ds)
  )

  #df with selected predictors and labels
  pred_features <- iatlas.api.client::query_cohort_features(cohorts = c(dataset_selection$train, dataset_selection$test)) %>%
    dplyr::filter(feature_name %in% selected_pred) %>%
    dplyr::mutate(VariableType = "Numeric") %>%
    dplyr::select(feature_name, feature_display, VariableType) %>%
    dplyr::distinct()

  if(nrow(pred_features) > 0){
    pred_df <- iatlas.api.client::query_feature_values(cohorts = cohort_obj$dataset_names, features = pred_features$feature_name) %>%
      dplyr::select(sample_name = sample, feature_name, feature_value)
  }else{
    pred_df <- NULL
  }

  #for categorical predictors, we need to make sure to store the key to label them correctly
  cat_df <- iatlas.api.client::query_tag_samples(cohorts = cohort_obj$dataset_names, parent_tags = c(selected_pred, "TCGA_Study", "Responder")) %>% #we always want TCGA Study to check if user mixed different types
    dplyr::inner_join(iatlas.api.client::query_tags_with_parent_tags(parent_tags = c(selected_pred, "TCGA_Study", "Responder")), by = c("tag_name", "tag_long_display", "tag_short_display", "tag_characteristics", "tag_color", "tag_order", "tag_type"))

  categories <- cat_df %>%
    dplyr::mutate(feature_name = paste0(parent_tag_name, tag_name), #parent_tag_name,
                  feature_display = tag_short_display, #tag_name,
                  VariableType = "Category") %>%
    dplyr::select(feature_name, feature_display, VariableType) %>%
    rbind(
      iatlas.api.client::query_tags_with_parent_tags(parent_tags = selected_pred)%>%
        dplyr::select(feature_name = parent_tag_name, feature_display = parent_tag_short_display) %>%
        dplyr::mutate(VariableType = "Categorical")
    ) %>%
    dplyr::distinct()

  cat_df <- dplyr::select(cat_df, sample_name, feature_name = parent_tag_name, feature_value = tag_name) #formatting to bind with others

  genes <- data.frame()
  if(length(selected_genes >0)) genes <- data.frame(
                                            feature_name = selected_genes,
                                            feature_display = selected_genes,
                                            VariableType = "Numeric")
  if(length(selected_genes >0)){
    gene_df <- iatlas.api.client::query_gene_expression(cohorts = cohort_obj$dataset_names, entrez = as.numeric(selected_genes))
    genes <- gene_df %>%
      dplyr::mutate(feature_name = hgnc,
                    feature_display = hgnc,
                    VariableType = "Numeric") %>%
      dplyr::select(feature_name, feature_display, VariableType) %>%
      dplyr::distinct()

    gene_df <- dplyr::select(gene_df, sample_name = sample, feature_name = hgnc, feature_value = rna_seq_expr)
  }else{
    genes <- NULL
    gene_df <- NULL
  }

  predictors <- rbind(pred_features, categories, genes)
  predictors_df <- rbind(pred_df,
                         cat_df,
                         gene_df)


  if("Prins_GBM_2019" %in% cohort_obj$dataset_names){
    pre_treat_samples <-  iatlas.api.client::query_tag_samples(tags = "pre_sample_treatment", cohorts = cohort_obj$dataset_names) %>%
      dplyr::bind_rows(iatlas.api.client::query_cohort_samples(cohorts = "Prins_GBM_2019")) %>% #we want all samples from Prins
      dplyr::distinct(sample_name)
  }else{
    pre_treat_samples <-  iatlas.api.client::query_tag_samples(cohorts = cohort_obj$dataset_names, tags = "pre_sample_treatment")
  }

  pred_df <- predictors_df %>%
    rbind(iatlas.api.client::query_feature_values(cohorts = cohort_obj$dataset_names, features = c("OS", "OS_time", "PFI_1", "PFI_time_1")) %>%
                       dplyr::select(sample_name = sample, feature_name, feature_value)) %>%
    dplyr::filter(sample_name %in% pre_treat_samples$sample_name) %>%
    tidyr::pivot_wider(., names_from = feature_name, values_from = feature_value, values_fill = NA) %>%
    dplyr::inner_join(iatlas.api.client::query_dataset_samples(datasets = cohort_obj$dataset_names), by = "sample_name")

  #subset dataset
  data_bucket <- list(
    train_df = pred_df %>%
      dplyr::filter(dataset_name %in% dataset_selection$train) %>%
      tidyr::drop_na(dplyr::any_of(predictors$feature_name)) %>%
      dplyr::filter(dplyr::if_all(previous_treat_to_exclude, exclude_treatment_data)),
    test_df = pred_df %>%
      dplyr::filter(dataset_name %in% dataset_selection$test) %>%
      tidyr::drop_na(dplyr::any_of(predictors$feature_name)) %>%
      dplyr::filter(dplyr::if_all(previous_treat_to_exclude, exclude_treatment_data))
  )

  #Check if any of the selected predictors is missing for a specific dataset (eg, IMVigor210 doesn't have Age data)
  missing_annot <- purrr::map_dfr(.x = c(dataset_selection$train, dataset_selection$test), predictor = dplyr::filter(predictors, VariableType != "Category")$feature_name, fmx_df = data_df, function(dataset, predictor, fmx_df){
    feature <- sapply(pred_df %>% dplyr::filter(dataset_name == dataset) %>% dplyr::select(predictor), function(x)sum(is.na(x)))

    if(length(feature[feature != 0])>0){
      n_samples <- nrow(pred_df[pred_df$dataset_name == dataset,])
      data.frame(feature_name = names(feature[feature != 0]),
                 dataset = dataset,
                 n_missing = feature[feature != 0]) %>%
        dplyr::mutate(
          missing_all = dplyr::case_when(
            n_missing == n_samples ~ 1,
            TRUE ~ 0
        )) %>%
        merge(., pred_features, by = "feature_name") %>%
        dplyr::select(feature_name, feature_display, dataset, n_missing, missing_all)
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
      missing_level <- setdiff(missing_test, missing_train)
      if(length(missing_level) != 0){
        missing_df <- data_bucket$test_df %>%
          dplyr::filter(.[[x]] %in% missing_level) %>%
          dplyr::group_by(dataset_name) %>%
          dplyr::select(dataset_name, dplyr::any_of(x)) %>%
          dplyr::distinct()

        cat_missing <- data.frame(
          feature_name = x,
          feature_display = subset(predictors, feature_name == x, feature_display),
          group = missing_df[[x]],
          dataset = missing_df$dataset_name
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
  }else{ #at the moment, only logistic regression
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
run_elastic_net <- function(train_df, response_variable, predictors, n_cv_folds, balance_lhs = TRUE, balance_rhs = FALSE, predictors_to_balance = NULL){
  print("training elastic net")

  predictors_to_model <- predictors %>% dplyr::filter(VariableType != "Category") %>% dplyr::pull(feature_name)
  cvIndex <- get_cv_folds(train_df, balance_lhs, balance_rhs, response_variable, predictors_to_balance, n_cv_folds)
  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors_to_model), collapse = "+")))

  model <- caret::train(
    parameters, data = train_df, method = "glmnet",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = 15
  )

  results <- get_table_cv_results(model, has_bestTune = TRUE)

  plot <- get_plot_var_importance(model, predictors, from_varImp = TRUE, scale_values = FALSE, title = "Absolute values of coefficients")

  list(model = model,
       results = results,
       plot = plot)
}

run_logistic_reg<- function(train_df, response_variable, predictors, n_cv_folds, balance_lhs = TRUE, balance_rhs = FALSE, predictors_to_balance = NULL){
  print("training model")
  predictors_to_model <- predictors %>% dplyr::filter(VariableType != "Category") %>% dplyr::pull(feature_name)
  cvIndex <- get_cv_folds(train_df, balance_lhs, balance_rhs, response_variable, predictors_to_balance, n_cv_folds)

  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors_to_model), collapse = "+")))
  model <- caret::train(
    parameters, data = train_df, method = "glm", family = "binomial",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = 15
  )

  results <- get_table_cv_results(model, has_bestTune = FALSE)

  plot <- get_plot_var_importance(model, predictors, from_varImp = FALSE, title = "Absolute values of coefficients")

  list(model = model,
       results = results,
       plot = plot)
}

run_xgboost <- function(train_df, response_variable, predictors, n_cv_folds, balance_lhs = TRUE, balance_rhs = FALSE, predictors_to_balance = NULL){
  print("training xgboost")
  predictors_to_model <- predictors %>% dplyr::filter(VariableType != "Category") %>% dplyr::pull(feature_name)
  cvIndex <- get_cv_folds(train_df, balance_lhs, balance_rhs, response_variable, predictors_to_balance, n_cv_folds)

  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors_to_model), collapse = "+")))
  model <- caret::train(
    parameters, data = train_df, method = "xgbTree",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = 5
  )

  results <- get_table_cv_results(model, has_bestTune = TRUE)

  plot <- get_plot_var_importance(model, predictors, from_varImp = TRUE, scale_values = TRUE, title = "Relative importance of predictors")

  list(model = model,
       results = results,
       plot = plot)
}

run_rf <- function(train_df, response_variable, predictors, n_cv_folds, balance_lhs = TRUE, balance_rhs = FALSE, predictors_to_balance = NULL){
  print("training random forest")
  predictors_to_model <- predictors %>% dplyr::filter(VariableType != "Category") %>% dplyr::pull(feature_name)
  cvIndex <- get_cv_folds(train_df, balance_lhs, balance_rhs, response_variable, predictors_to_balance, n_cv_folds)

  parameters <- as.formula(paste(response_variable, "~ ", paste0(sprintf("`%s`", predictors_to_model), collapse = "+")))
  model <- caret::train(
    parameters, data = train_df, method = "rf",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = length(predictors)
  )

  results <- get_table_cv_results(model, has_bestTune = TRUE)

  plot <- get_plot_var_importance(model, predictors, from_varImp = TRUE, scale_values = TRUE, title = "Relative importance of predictors")

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
            dplyr::filter(dataset_name == x) %>%
            dplyr::mutate(prediction = predict(model, newdata = .))

    accuracy_results <- caret::confusionMatrix(df$prediction, as.factor(df$Responder), positive = "true_responder")

    rocp <- pROC::roc(
      response = factor(df$Responder,  ordered = TRUE),
      predictor = factor(df$prediction, ordered = TRUE),
      levels = c("true_responder", "false_responder"),
      quiet = TRUE,
      auc = TRUE)

    rplot <- pROC::ggroc(rocp) + ggplot2::labs(title = paste("AUC: ", round(rocp$auc, 3)))
    #KM plot
    dataset_df <- survival_data %>%
        select(sample_name, OS, OS_time, PFI_1, PFI_time_1) %>%
        merge(., df, by = "sample_name")
    dataset_df[, c("OS", "OS_time", "PFI_1", "PFI_time_1")] <- sapply(dataset_df[, c("OS", "OS_time", "PFI_1", "PFI_time_1")], as.numeric)
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

