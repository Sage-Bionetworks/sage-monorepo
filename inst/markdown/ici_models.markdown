Drag and drop ICI Datasets of interest in *1. Select training and testing datasets* to be used either for training or testing of the model. In *2. Select predictors*, select features of interest to be included as predictors in your models. Advanced options, such as scaling of numerical predictors and balancing of categorical classes, are optional and available in the *Advanced* box (Click on + to expand the options).

After you select datasets and predictors, you can choose two modeling methods to train a model, and the number of folds for cross validation. Selection of datasets, predictors, and advanced settings will be used for both models.

Press *Train Model* for each model that you want to run, and press it again to update the model each time changes in selection are made. Summary of model parameters (when applicable), accuracy over cross validation, and importance of predictors will be shown.

When you determine that your trained model is ready to be tested against the testing datasets, press *Run model in the test dataset(s)*. You will see a KM plot stratifying patients based on the predicted response, an AUC plot, and a confusion matrix with associated statistics for each one of the datasets selected for testing.

You can click here for more information on the method for merging the data and running the model. Notebooks replicating the analysis are available in our [iatlas-notebooks](https://github.com/CRI-iAtlas/iatlas-notebooks) repository.








