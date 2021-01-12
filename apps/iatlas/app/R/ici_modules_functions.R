#utils functions from shiny-iatlas

assert_df_has_columns <- function(df, columns){
  missing_columns <- columns[!columns %in% colnames(df)]
  if(length(missing_columns) != 0){
    stop("df has missing columns: ",
         stringr::str_c(missing_columns, collapse = ", "))
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

# functions for multiple ici modules

get_group_labels <-  function(df, group){
  df %>%
    dplyr::filter(Category == group) %>%
    dplyr::select(FeatureValue, FeatureLabel, FeatureHex, order_within_sample_group)
}

