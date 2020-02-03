library(magrittr)

source("R/functions/connect_to_db.R")
pool <- connect_to_db()
rm(connect_to_db)

source("R/functions/utils.R")
source("R/functions/format.R")
source("R/functions/plot_functions/scatterplot.R")
source("R/functions/plot_functions/violinplot.R")
source("R/functions/plot_functions/mosaicplot.R")
source("R/functions/plot_functions/barplot.R")
source("R/functions/plot_functions/boxplot.R")
source("R/functions/plot_functions/heatmap.R")
source("R/functions/plot_functions/kmplot.R")
source("R/functions/plot_functions/histogram.R")





