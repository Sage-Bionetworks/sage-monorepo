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
    query_dir = "queries",
    qry_obj   = .GlobalEnv$ghql_query_object
){
    create_global_ghql_query_object()
    query_files <- list.files(query_dir, full.names = T)
    query_names <- query_files %>%
        basename() %>%
        stringr::str_remove_all(., ".txt")
    purrr::walk2(query_names, query_files, add_ghql_query_from_text_file)
}

connect_to_api <- function(url = 'http://localhost:5000/api'){
    ghql::GraphqlClient$new(url)
}

perform_api_query <- function(
    query_name,
    variables,
    qry_obj = .GlobalEnv$ghql_query_object
){
    con <- connect_to_api()
    result <-
        con$exec(
            qry_obj$queries[[query_name]],
            variables
        ) %>%
        jsonlite::fromJSON()
    return(result)
}
