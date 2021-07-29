build_survival_df <- function(df, group_column, group_options, time_column, div_range, k) {

  get_groups <- function(df, group_column, k) {

    if(is.numeric(df[[group_column]])){

      if (div_range == 'median') {

        as.character( ifelse (df[[group_column]] < median(df[[group_column]], na.rm=T),
                              yes='lower half', no='upper half') )

      } else {

        as.character(cut(df[[group_column]], k, ordered_result = T))
      }
    } else {

      as.character(df[[group_column]])

    }
  }

  # get the vectors associated with each term
  # if facet_column is already a catagory, just use that.
  # otherwise it needs to be divided into k catagories.
  groups <- get_groups(df, group_column, k)
  if (time_column == "OS_time") {
    status_column <- "OS"
  } else {
    status_column <- "PFI_1"
  }

  data.frame(
    status = purrr::pluck(df, status_column),
    time = purrr::pluck(df, time_column),
    group = groups,
    variable = groups,
    measure = purrr::pluck(df, group_column)
  ) %>%
    na.omit()
}
