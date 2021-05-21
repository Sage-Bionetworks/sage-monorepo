datasets_options <-  list(
  "Gide, 2019 - SKCM, Anti-PD-1 +/- Anti-CTLA-4" =  "Gide 2019",
  "Hugo, 2016 - SKCM, Anti-PD-1" = "Hugo 2016",
  "Riaz, 2017 - SKCM, Anti-PD-1" = "Riaz 2017",
  "Van Allen, 2015 - SKCM, Anti-CTLA-4" = "Van Allen 2015",
  "IMVigor210 - BLCA, Anti-PD-L1" = "IMVigor210",
  "Prins, 2019 - GBM, Anti-PD-1" = "Prins 2019")

datasets_options_train <-  list(
  "Gide, 2019 - SKCM, Anti-PD-1 +/- Anti-CTLA-4 (72)" =  "Gide 2019",
  "Hugo, 2016 - SKCM, Anti-PD-1 (28)" = "Hugo 2016",
  "Riaz, 2017 - SKCM, Anti-PD-1 (49)" = "Riaz 2017",
  "Van Allen, 2015 - SKCM, Anti-CTLA-4 (42)" = "Van Allen 2015",
  "IMVigor210 - BLCA, Anti-PD-L1 (348)" = "IMVigor210",
  "Prins, 2019 - GBM, Anti-PD-1 (29)" = "Prins 2019")

datasets_PFI <- c("Gide 2019", "Van Allen 2015", "Prins 2019")

ioresponse_data <- load_io_data()


#utils functions from shiny-iatlas

assert_df_has_columns <- function(df, columns){
  missing_columns <- columns[!columns %in% colnames(df)]
  if(length(missing_columns) != 0){
    stop("df has missing columns: ",
         stringr::str_c(missing_columns, collapse = ", "))
  }
}

assert_df_has_rows <- function(df){
  if(nrow(df) == 0){
    stop("result df is empty")
  }
}

convert_values <- function(values, df, from_column, to_column){
  assert_df_has_columns(df, c(from_column, to_column))
  df %>%
    dplyr::select(FROM = from_column, TO = to_column) %>%
    dplyr::filter(FROM %in% values) %>%
    magrittr::use_series(TO)
}

convert_value_between_columns <- function(
  input_value, df, from_column, to_column,
  no_matches = "error",
  many_matches = "error"){

  result <- convert_values(
    input_value, df, from_column, to_column)

  # 1 match
  if (length(result) == 1) return(result)

  # no matches
  if (length(result) == 0){
    if(no_matches == "return_input"){
      return(input_value)
    } else if(no_matches == "return_na") {
      return(NA)
    } else {
      stop("input value has no matches: ", input_value)
    }
  }

  # many matches
  if (length(result) > 1){
    if(many_matches == "return_result"){
      return(result)
    } else {
      stop("input value: ",
           input_value,
           ", has multiple matches: ",
           stringr::str_c(result, collapse = ", "))
    }
  }
}

create_filtered_nested_list_by_class <- function(
  feature_df,
  filter_value,
  class_column = "CLASS",
  display_column = "DISPLAY",
  internal_column = "INTERNAL",
  filter_column = "FILTER"){

  feature_df %>%
    dplyr::select(
      CLASS = class_column,
      DISPLAY = display_column,
      INTERNAL = internal_column,
      FILTER = filter_column) %>%
    dplyr::filter(FILTER == filter_value) %>%
    select(CLASS, INTERNAL, DISPLAY) %>%
    create_nested_list_by_class()
}

create_nested_list_by_class <- function(
  df,
  class_column = "CLASS",
  display_column = "DISPLAY",
  internal_column = "INTERNAL",
  filter_expr = T)
{
  df %>%
    dplyr::filter({{filter_expr}}) %>%
    dplyr::select(
      CLASS = class_column,
      DISPLAY = display_column,
      INTERNAL = internal_column
    ) %>%
    dplyr::mutate(CLASS = ifelse(is.na(CLASS), "Other", CLASS)) %>%
    dplyr::mutate(CLASS = ifelse(CLASS == "", "Other", CLASS)) %>%
    df_to_nested_list(
      group_column = "CLASS",
      key_column = "INTERNAL",
      value_column = "DISPLAY")
}

df_to_nested_list <- function(df, group_column, key_column, value_column){
  df %>%
    get_complete_df_by_columns(c(group_column, value_column, key_column)) %>%
    tidyr::nest(data = c(value_column, key_column)) %>%
    dplyr::mutate(data = purrr::map(data, tibble::deframe)) %>%
    tibble::deframe()
}

get_complete_df_by_columns <- function(df, columns){
  assert_df_has_columns(df, columns)
  result_df <- df %>%
    dplyr::select(columns) %>%
    tidyr::drop_na()
  assert_df_has_rows(result_df)
  return(result_df)
}

# functions for multiple ici modules

get_group_labels <-  function(df, group, group_labels = ioresponse_data$sample_group_df){
  group_labels %>%
    dplyr::filter(Category == group) %>%
    dplyr::select(FeatureValue, FeatureLabel, FeatureHex, order_within_sample_group)
}

#functions to add lines for categorical groups

get_lines_pos <- function(samples, y){

  n_int <- nrow(samples)

  divs <-seq(0, 1, len=n_int+1)

  #getting the intervals with the same variable
  int_pos <- divs[1]
  for(i in 1:n_int){
    try(if(samples$var1[i] != samples$var1[i+1]) int_pos <- c(int_pos, divs[i+1]), silent = TRUE)
  }
  int_pos <- c(int_pos, divs[n_int+1])

  lines_pos <- "list("
  for (i in 1:(length(int_pos)-1)) {

    lines_pos <- paste(lines_pos,
                       "list(line = list(color = 'rgba(68, 68, 68, 0.5)', width = 1), type = 'line', x0 =",
                       (int_pos[i]+0.01),
                       ", x1 =",
                       (int_pos[i]+0.01),
                       ", xref = 'paper', y0 =",
                       0.2,
                       ", y1 =",
                       - 0.1,
                       ", yref = 'paper'),
                       list(line = list(color = 'rgba(68, 68, 68, 0.5)', width = 1), type = 'line', x0 =",
                       (int_pos[i+1]),
                       ", x1 =",
                       (int_pos[i+1]),
                       ", xref = 'paper', y0 =",
                       0.2,
                       ", y1 =",
                       - 0.1,
                       ", yref = 'paper')
                       "
                       )
    if(i != (length(int_pos)-1)) lines_pos <- paste(lines_pos, ",")
  }
  paste(lines_pos, ")")
}

get_hlines_pos <- function(samples){

  n_int <- nrow(samples)

  divs <-seq(0, 1, len=n_int+1)

  #getting the intervals with the same variable
  int_pos <- as.numeric()#divs[1]
  for(i in 1:n_int){
    try(if(samples$var1[i] != samples$var1[i+1]) int_pos <- c(int_pos, divs[i+1]), silent = TRUE)
  }
  int_pos <- c(int_pos, divs[n_int+1])

  lines_pos <- "list("
  for (i in 1:(length(int_pos)-1)) {

    lines_pos <- paste(lines_pos,
                       "list(line = list(color = 'rgba(68, 68, 68, 0.5)', width = 1), type = 'line', x0 =",
                       0,
                       ", x1 =",
                       -0.5,
                       ", xref = 'paper', y0 =",
                       (int_pos[i]),
                       ", y1 =",
                       (int_pos[i]),
                       ", yref = 'paper')
                       "
    )
    if(i != (length(int_pos)-1)) lines_pos <- paste(lines_pos, ",")
  }
  paste(lines_pos, ")")
}

# ICI Clinical Outcomes
# TODO: Creating a list of KM plots for each selected dataset

