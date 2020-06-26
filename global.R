library(magrittr)

source("R/database_functions.R")
pool <- connect_to_db()
rm(connect_to_db)

source("R/api_functions.R")
iatlas.app::create_and_add_all_queries_to_qry_obj()

source("R/create_queries.R")
source("R/perform_query.R")
source("R/run_queries.R")
source("R/utils.R")
source("R/functions/format.R")
source("R/functions/scatterplot.R")
source("R/functions/violinplot.R")
source("R/functions/mosaicplot.R")
source("R/functions/barplot.R")
source("R/functions/boxplot.R")
source("R/functions/heatmap.R")
source("R/functions/kmplot.R")
source("R/functions/histogram.R")





