add_ghql_query_from_text_file <- function(
    query_name,
    text_file,
    query_object = .GlobalEnv$ghql_query_object
){
    query_text <- text_file %>%
        readLines() %>%
        stringr::str_c(collapse = "\n")
    query_object$query(query_name, query_text)
}

create_global_ghql_query_object <- function() {
    if (!present(.GlobalEnv$ghql_query_object)) {
        .GlobalEnv$ghql_query_object <- ghql::Query$new()
    } else {
        cat(crayon::yellow(
            "WARNING-create_global_ghql_query_object: global ghql query object already created\n")
        )
        .GlobalEnv$ghql_query_object
    }
}

create_and_add_all_queries_to_qry_obj <- function(
    query_dir = "inst/queries",
    qry_obj   = .GlobalEnv$ghql_query_object
){
    create_global_ghql_query_object()
    query_files <- list.files(query_dir, full.names = T)
    query_names <- query_files %>%
        basename() %>%
        stringr::str_remove_all(., ".txt")
    purrr::walk2(query_names, query_files, add_ghql_query_from_text_file)
}

perform_api_query <- function(
    query_name,
    variables,
    qry_obj = .GlobalEnv$ghql_query_object,
    api_url = "http://ec2-54-190-27-240.us-west-2.compute.amazonaws.com/api"
){
    if (!query_name %in% names(qry_obj$queries)) {
        msg <- qry_obj$queries %>%
            names %>%
            stringr::str_c(collapse = ", ") %>%
            stringr::str_c("Query: ", query_name, " not in avaialble queries: ", .)

        stop(msg)
    }
    query <- qry_obj$queries[[query_name]]
    con <- ghql::GraphqlClient$new(api_url)
    result <-
        con$exec(query, variables) %>%
        jsonlite::fromJSON() %>%
        purrr::pluck("data")
    return(result)
}
