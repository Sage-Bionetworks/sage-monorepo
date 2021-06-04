load_io_response <- function(){
    list(
      fmx_io = arrow::read_feather("inst/feather/fmx_io.feather"),
      dataset_io_df = arrow::read_feather("inst/feather/datasets_io_df.feather"),
      categories_io_df = arrow::read_feather("inst/feather/categories_df.feather"),
      sample_group_io_df = arrow::read_feather("inst/feather/io_sample_group_df.feather"),
      feature_io_df = arrow::read_feather("inst/feather/feature_io_df.feather"),
      im_expr_io_df = arrow::read_feather("inst/feather/im_expr_io.feather")
    )
}

load_io_data <- function(){
  io_data <- load_io_response()

  list(
    dataset_df = io_data$dataset_io_df,
    feature_df = io_data$feature_io_df,
    categories_df = io_data$categories_io_df,
    sample_group_df = io_data$sample_group_io_df,
    fmx_df = io_data$fmx_io,
    im_expr = io_data$im_expr_io_df
  )
}
