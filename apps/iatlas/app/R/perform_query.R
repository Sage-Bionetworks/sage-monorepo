#' Perform Query
#'
#' @param query A string that is a valid query
#' @param message A string that is displayed when run
#' @param db_pool A database ppol of type pool::dbPool
#' @importFrom tictoc tic toc
#' @importFrom pool poolCheckout dbGetQuery poolReturn
perform_query <- function(query, message = "", db_pool = .GlobalEnv$pool) {
    # tictoc::tic(paste(
    #     "Time taken to perfrom query",
    #     message
    # ))
    current_pool <- pool::poolCheckout(db_pool)
    tbl <- query %>%
        dplyr::sql() %>%
        pool::dbGetQuery(current_pool, .) %>%
        dplyr::as_tibble()
    pool::poolReturn(current_pool)
    # tictoc::toc()
    return(tbl)
}
