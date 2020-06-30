query_cohort_selector <- function(
    dataset = "TCGA",
    group_tag = "Immune_Subtype"
){
    iatlas.app::perform_api_query(
        "cohort_selection",
        list(
            dataSet = dataset,
            related = group_tag,
            feature = list(),
            featureClass = list()
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()
}

query_features_by_class <- function(
    dataset = list(),
    group_tag = list(),
    feature = list(),
    feature_class = list()
){
    iatlas.app::perform_api_query(
        "features_by_class",
        list(
            dataSet = dataset,
            related = group_tag,
            feature = feature,
            featureClass = feature_class
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()
}
