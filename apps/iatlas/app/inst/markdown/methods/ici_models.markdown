**Title:** Multivariable Models with ICI datasets

**Description:**

*Conditions for inclusion in the training and testing data:* Given a selection of datasets to train and test a model, samples from these datasets will be included if they have the following characteristics:
- Sample was collected pre ICI treatment
- Sample has  mRECIST annotation
- There is no NA value for any of the selected predictors

Datasets selected for training will be merged after normalization.


*Normalization*: In case normalization of numeric predictors is selected, normalization will be computed by subtracting each observation from the mean and dividing by the standard deviation. In the training data, the mean and standard deviation are computed independently in each training dataset. For the testing data, the mean and standard deviation of a given predictor correspond to the value for the whole training set (ie, after merging all datasets selected for training).


*Balancing categorical variables:* By default, each fold in cross-validation will have a previous step of balancing for the selected Response variable (you can turn it off in the Advanced options). Other selected categorical predictors can also be included in this process. Balancing is performed by the function `createFolds` in the `caret` package (Kuhn, 2020).


*Training and cross-validation:* We use functions from the `caret` package (Kuhn, 2020), as described below.

- *Elastic Net*
```
model <- caret::train(
    parameters, data = train_df, method = "glmnet",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = 15
  )
```

- *Logistic regression*
```
model <- caret::train(
    parameters, data = train_df, method = "glm", family = "binomial",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = 15
  )
```

- *Random Forest*
```
model <- caret::train(
    parameters, data = train_df, method = "rf",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = length(predictors)
  )
```

- *XGBoost*
```
model <- caret::train(
    parameters, data = train_df, method = "xgbTree",
    trControl = caret::trainControl(index = cvIndex, "cv", number = n_cv_folds),
    tuneLength = 5
  )
```

Where:

`train_df `= training data after normalization

`cvIndex` = Index of samples for each fold, after balancing

`n_cv_folds` = user-selected number of folds for cross validation

`predictors` = user-selected predictors to be included in the model


*Testing:* After the training is completed, the model can be used to predict the response of samples in the testing datasets. The prediction is made using the trained model, the normalized testing dataset and the function `predict` from the caret package. The performance of the model in the testing dataset can then be assessed by  a KM plot stratifying patients based on the predicted response, an AUC plot, and a confusion matrix with associated statistics for each one of the datasets selected for testing.

- *Confusion matrix and statistics:* A confusion matrix with associated statistics is computed by the `confusionMatrix` function in the `caret` package.


- *ROC curve:* A ROC curve is made available and is computed by the `roc` function in the `pROC` package (Robin et al, 2011), as described below:

```
rocp <- pROC::roc(
      response = factor(df[[training_obj$response_var]],  ordered = TRUE),
      predictor = factor(df$prediction, ordered = TRUE),
      levels = names(class_labels),
      quiet = TRUE,
      auc = TRUE)
```

- *KM plot:* The KM plot shows survival curves stratifying patients based on the response predicted by the model, alongside to the log-rank test p-value, as computed by the `survminer::ggsurvplot()` function (Kassambara et al, 2021).


**References**

Kassambara, A; Kosinski, M; Biecek, P  (2021). survminer: Drawing Survival Curves using 'ggplot2'. R package version 0.4.9. https://CRAN.R-project.org/package=survminer

Kuhn, Max (2020). caret: Classification and Regression Training. R package version 6.0-86. https://CRAN.R-project.org/package=caret

Robin, X; Turck, N;  Hainard, A; Tiberti, N; Lisacek, F; Sanchez, JC and Müller, M (2011). pROC: an open-source package for R and S+ to analyze and compare ROC curves. BMC Bioinformatics, 12, p. 77.  DOI: 10.1186/1471-2105-12-77 <http://www.biomedcentral.com/1471-2105/12/77/>


