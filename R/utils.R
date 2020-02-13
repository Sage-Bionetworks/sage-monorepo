#' Transform Feature String
#'
#' @param feature A string, the name of the feature
#' @param transformation A string, the name of the transformation
transform_feature_string <- function(feature, transformation){
    switch(
        transformation,
        "None"       = feature,
        "Log2"       = paste("Log2(",   feature,  ")"),
        "Log2 + 1"   = paste("Log2(",   feature,  "+ 1 )"),
        "Log10"      = paste("Log10(",  feature,  ")"),
        "Log10 + 1"  = paste("Log10(",  feature,  "+ 1 )"),
        "Squared"    = paste0(feature, "**2"),
        "Reciprocal" = paste0("1/", feature)
    )
}

#' Transform Feature Formula
#'
#' @param feature A string, the name of the feature
#' @param transformation A string, the name of the transformation
transform_feature_formula <- function(feature, transformation){
    switch(
        transformation,
        "None"       = feature,
        "Squared"    = paste0("I(",       feature, "**2)"),
        "Log10"      = paste0("I(log10(", feature, "))"),
        "Reciprocal" = paste0("I(1/",     feature, ")")
    )
}

#' Assert Tibble Has Columns
#'
#' @param tbl A tibble
#' @param columns a vector of columns
assert_tbl_has_columns <- function(tbl, columns){
    missing_columns <- columns[!columns %in% colnames(tbl)]
    if (length(missing_columns) != 0) {
        stop("tbl has missing columns: ",
             paste0(missing_columns, collapse = ", "))
    }
}

#' Assert Tibble has Rows
#'
#' @param tbl A tibble
assert_tbl_has_rows <- function(tbl){
    if (nrow(tbl) == 0) {
        stop("result tbl is empty")
    }
}

#' Title
#'
#' @param tbl A tibble
#' @param title A string
#' @param name Name of a column
#' @param group Name of a column
#' @importFrom rlang .data
#' @importFrom dplyr mutate
add_plotly_label <- function(tbl, title, name, group){
    dplyr::mutate(tbl, label = paste0(
        "<b>", title, ":</b> ", {{name}}, " (", {{group}}, ")"
    ))
}

#' Add Plotly Value Label
#'
#' @param tbl A tibble with column label
#' @param cols A vector of strings that are columns in the tibble
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom tidyr pivot_longer pivot_wider
#' @importFrom dplyr mutate group_by ungroup
add_plotly_value_label <- function(tbl, cols){
    tbl %>%
        tidyr::pivot_longer(
            .,
            cols,
            names_to  = "value_name",
            values_to = "value"
        ) %>%
        dplyr::mutate(value_label = stringr::str_glue(
            "{name}: {value}",
            name = stringr::str_to_upper(.data$value_name),
            value = sprintf("%0.3f", .data$value)
        )) %>%
        dplyr::group_by(.data$label) %>%
        dplyr::mutate(value_label = paste0(
            .data$value_label,
            collapse = "</br>"
        )) %>%
        dplyr::ungroup() %>%
        tidyr::pivot_wider(
            .,
            names_from = .data$value_name,
            values_from = .data$value
        )
}


#' Create Plotly Label
#'
#' @param tbl A tibble
#' @param name A column
#' @param group A column
#' @param cols A vector of strings, whioch are columns in the tibble
#' @param title A string
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom tidyr unite
create_plotly_label <- function(
    tbl,
    name,
    group,
    cols,
    title = "ParticipantBarcode"
){

    tbl %>%
        add_plotly_label(title, {{name}}, {{group}}) %>%
        add_plotly_value_label(cols) %>%
        tidyr::unite(
            "label",
            .data$label,
            .data$value_label,
            sep = "</br></br>"
        )
}

#' Get Unique Values from Column
#'
#' @param tbl A tibble
#' @param col A column in the tibble
#' @importFrom dplyr select distinct pull
#' @importFrom tidyr drop_na
#' @importFrom rlang .data
get_unique_values_from_col <- function(tbl, col){
    tbl %>%
        dplyr::select({{col}}) %>%
        tidyr::drop_na() %>%
        dplyr::distinct() %>%
        dplyr::pull({{col}})
}


#' Create Feature Named List
#'
#' @param class_ids Integers in the id column of the classes table
#' @importFrom magrittr %>%
create_feature_named_list <- function(class_ids = "all"){
    list <-
        build_feature_tbl(class_ids) %>%
        create_nested_named_list()
    return(list)
}

#' Create Nested Named List
#'
#' @param tbl A tibble with the below columns
#' @param names_col1 A column that will be the names of the top list
#' @param names_col2 A column that will be the names of the nested lists
#' @param values_col A column that will be the values of the nested lists
#' @importFrom magrittr %>%
#' @importFrom dplyr select mutate
#' @importFrom tidyr drop_na nest
#' @importFrom tibble deframe
#' @importFrom purrr map
#' @importFrom rlang .data
create_nested_named_list <- function(
    tbl,
    names_col1 = "class",
    names_col2 = "display",
    values_col = "feature"
){
    list <- tbl %>%
        dplyr::select(
            n1 = names_col1,
            n2 = names_col2,
            v  = values_col
        ) %>%
        tidyr::drop_na() %>%
        tidyr::nest(data = c(.data$n2, .data$v)) %>%
        dplyr::mutate(data = purrr::map(.data$data, tibble::deframe)) %>%
        tibble::deframe(.)
    return(list)
}


# transforms ------------------------------------------------------------------

#' Scale Tibble Value Column
#'
#' @param tbl A Tibble with column "value"
#' @param scale_method One of "Log2", "Log2 + 1", "Log10 + 1", "Log10"
scale_tbl_value_column <- function(tbl, scale_method = "None"){
    if (scale_method %in% c("Log2", "Log2 + 1", "Log10 + 1", "Log10")) {
        add_amt <- 0
        base    <- 10
        if (scale_method %in% c("Log2", "Log2 + 1")) {
            base <- 2
        }
        if (scale_method %in% c("Log10 + 1", "Log2 + 1")) {
            add_amt <- 1
        }
        tbl <- log_tbl_value_column(tbl, base, add_amt)
    } else if (scale_method == "None") {
        tbl <- tbl
    } else {
        stop("Scale method does not exist")
    }
    return(tbl)
}

#' Log Tibble Value Column
#'
#' @param tbl A Tibble with column "value"
#' @param base An integer, used as the base in log
#' @param add_amt A numeric, added to the vlaue column before logging
#' @importFrom magrittr %>%
#' @importFrom dplyr mutate filter
#' @importFrom rlang .data
log_tbl_value_column <- function(tbl, base = 10, add_amt = 0){
    tbl %>%
        dplyr::mutate(value = .data$value + add_amt) %>%
        dplyr::filter(.data$value > 0) %>%
        dplyr::mutate(value = log(.data$value, base))
}

# event data utils -------------------------------------------------------------

#' Get Values from Eventdata Dataframe
#'
#' @param eventdata Eventdata from "plotly_click" plotly::event_data
#' @param col The column to get the values from
#' @importFrom magrittr %>% extract2
#' @importFrom dplyr as_tibble
get_values_from_eventdata <- function(eventdata, col = "x"){
    eventdata %>%
        dplyr::as_tibble() %>%
        magrittr::extract2(col) %>%
        unique()
}
