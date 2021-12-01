load_io_response <- function(){
    list(
      dataset_io_df = arrow::read_feather("inst/feather/datasets_io_df.feather")
    )
}

load_io_data <- function(){
  io_data <- load_io_response()

  list(
    dataset_df = io_data$dataset_io_df
  )
}
