
# David L Gibbs
# dgibbs@systemsbiology.org

# this script will form the backend of the shiny app.

library(ImmuneSubtypeClassifier)

readNewDataTable <- function(fileinfo, sepflag) {

  if(is.null(fileinfo)) {
    fileinfo <- list(name='ebpp_test1_1to20.tsv',  size=1, type='text/csv', datapath='inst/tsv/ebpp_test1_1to20.tsv')
  }

  newdata <- read.table(file=fileinfo$datapath, sep=sepflag, header=T, stringsAsFactors = F)

  newX <- as.matrix(newdata[,-1])
  rownames(newX) <- as.character(newdata[,1])

  newX
}

# this function computes scores given some expression data.
classifySubtype <- function(newdata) {

  # make cluster calls using the models.
  calls <- ImmuneSubtypeClassifier::callEnsemble(X = newdata, geneids = 'symbol')

  print(utils::head(calls))

  calls[,3] <- round(calls[,3], digits = 3)
  calls[,4] <- round(calls[,4], digits = 3)
  calls[,5] <- round(calls[,5], digits = 3)
  calls[,6] <- round(calls[,6], digits = 3)
  calls[,7] <- round(calls[,7], digits = 3)
  calls[,8] <- round(calls[,8], digits = 3)

  print("Done")

  return(list(Calls = calls))

}


