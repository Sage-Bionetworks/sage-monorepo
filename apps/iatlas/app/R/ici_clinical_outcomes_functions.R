build_survival_df <- function(df, group_column, group_options, time_column, div_range, k, group_choice = NULL, group_subset = NULL) {

  # subset to a smaller group of samples #
  if(!is.null(group_choice)){
    if (group_choice == 'Study' & group_subset != 'All') {

      df <- df  %>% dplyr::filter(Study == UQ(group_subset))

    } else if (group_choice == 'Subtype_Immune_Model_Based' & group_subset != 'All') {

      df <- df %>% dplyr::filter(Subtype_Immune_Model_Based == UQ(group_subset))

    } else if (group_choice == 'Subtype_Curated_Malta_Noushmehr_et_al' & group_subset != 'All') {

      df <- df %>% dplyr::filter(Subtype_Curated_Malta_Noushmehr_et_al == UQ(group_subset))

    }
  }

  get_groups <- function(df, group_column, k) {

    if (group_column %in% group_options) { # then we don't need to produce catagories.

      as.character(df[[group_column]])
    }
    else {

      if (div_range == 'median') {

        as.character( ifelse (df[[group_column]] < median(df[[group_column]], na.rm=T),
                              yes='lower half', no='upper half') )

      } else {

        as.character(cut(df[[group_column]], k, ordered_result = T))
      }

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
