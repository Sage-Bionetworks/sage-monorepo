perform_query <- function(query, string = "") {
    tictoc::tic(paste(
        "Time taken to perfrom query",
        string
    ))
    current_pool <- pool::poolCheckout(.GlobalEnv$pool)
    tbl <- query %>%
        dplyr::sql() %>%
        pool::dbGetQuery(current_pool, .) %>%
        dplyr::as_tibble()
    pool::poolReturn(current_pool)
    tictoc::toc()
    return(tbl)
}

get_feature_id_from_display <- function(display){
    display %>%
        create_get_feature_id_from_display_query() %>%
        perform_query("Get feature id from display") %>%
        dplyr::pull(id)
}

get_feature_display_from_id <- function(id){
    id %>%
        create_get_feature_display_from_id_query() %>%
        perform_query("Get feature display from id") %>%
        dplyr::pull(display)
}

get_feature_values_from_ids <- function(ids){
    ids %>%
        create_feature_value_query_from_ids() %>%
        perform_query("Get feature values from ids")
}

get_class_id_from_name <- function(name){
    name %>%
        create_get_class_id_from_name_query %>%
        perform_query("Get class id from name") %>%
        dplyr::pull(id)
}

create_class_list <- function(){
    "SELECT name, id FROM classes" %>%
        perform_query("get class id") %>%
        tibble::deframe()
}
