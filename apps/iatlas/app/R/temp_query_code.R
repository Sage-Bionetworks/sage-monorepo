# con <- ghql::GraphqlClient$new('http://localhost:5000/api')
#
# qry <- ghql::Query$new()
#
#
# qry$query(
#     'cohort_selector',
#     stringr::str_c(readLines("queries/cohort_selection.txt"), collapse = "\n")
# )
#
# res <-
#     con$exec(
#         .GlobalEnv$ghql_query_object$queries[["cohort_selection"]],
#         list(
#             dataSet = 'TCGA',
#             related = "Immune_Subtype",
#             feature = list(),
#             featureClass = list()
#         )
#     ) %>%
#     jsonlite::fromJSON() %>%
#     purrr::pluck("data", 1)




