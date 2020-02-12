library(magrittr)

source("R/connect_to_db.R")
pool <- connect_to_db()
rm(connect_to_db)

source("R/create_queries.R")
source("R/perform_query.R")
source("R/run_queries.R")
source("R/utils.R")
source("R/functions/format.R")
source("R/functions/plot_functions/scatterplot.R")
source("R/functions/plot_functions/violinplot.R")
source("R/functions/plot_functions/mosaicplot.R")
source("R/functions/plot_functions/barplot.R")
source("R/functions/plot_functions/boxplot.R")
source("R/functions/plot_functions/heatmap.R")
source("R/functions/plot_functions/kmplot.R")
source("R/functions/plot_functions/histogram.R")





