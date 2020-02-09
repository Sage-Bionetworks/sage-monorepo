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
