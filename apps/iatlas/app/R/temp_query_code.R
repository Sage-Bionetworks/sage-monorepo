# con <- ghql::GraphqlClient$new("http://ec2-54-190-27-240.us-west-2.compute.amazonaws.com/api")
#
# qry <- ghql::Query$new()
#
#
# qry$query(
#     'cohort_selection',
#     stringr::str_c(readLines("queries/cohort_selection.txt"), collapse = "\n")
# )
#
# res <-
#     con$exec(qry$queries[["cohort_selection"]],
#         list(
#             dataSet = 'TCGA',
#             related = "Immune_Subtype",
#             feature = list(),
#             featureClass = list()
#         )
#     ) %>%
#     jsonlite::fromJSON() %>%
#     purrr::pluck("data", 1)




