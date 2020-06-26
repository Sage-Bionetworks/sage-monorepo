query_cohort_selector <- function(
    dataset = "TCGA",
    group_tag = "Immune_Subtype"
){
    perform_api_query(
        "cohort_selection",
        list(
            dataSet = dataset,
            related = group_tag,
            feature = list(),
            featureClass = list()
        )
    ) %>%
        purrr::pluck("data", 1) %>%
        dplyr::as_tibble()
}
