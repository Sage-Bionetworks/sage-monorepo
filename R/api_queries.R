# related ---------------------------------------------------------------------

query_dataset_tags <- function(dataset){
    iatlas.app::perform_api_query(
        "dataset_tags",
        list(
            dataSet = dataset,
            related = list()
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        tidyr::unnest(cols = c("related")) %>%
        dplyr::select("display", "name") %>%
        dplyr::arrange(.data$name)
}

# tags ------------------------------------------------------------------------

query_tags <- function(dataset, parent_tag){
    iatlas.app::perform_api_query(
        "tags",
        list(
            dataSet = dataset,
            related = parent_tag
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        dplyr::select("name", "display") %>%
        dplyr::arrange(.data$name)
}

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
        dplyr::as_tibble() %>%
        tidyr::unnest(cols = c("samples")) %>%
        dplyr::select(
            "name",
            "display",
            "characteristics",
            "color",
            "size" = "sampleCount",
            "sample" = "samples"
        )
}

# features --------------------------------------------------------------------

query_feature_values <- function(
    dataset = list(),
    group_tag = list(),
    feature = list()
){
    iatlas.app::perform_api_query(
        "feature_values",
        list(
            dataSet = dataset,
            related = group_tag,
            feature = feature
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()
}

# features_by_tag --------------------------------------------------------------

query_feature_values_by_tag <- function(
    dataset = list(),
    group_tag = list(),
    feature = list()
){
    iatlas.app::perform_api_query(
        "feature_values_by_tag",
        list(
            dataSet = dataset,
            related = group_tag,
            feature = feature
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()
}

query_features_values_by_tag <- function(
    dataset = list(),
    group_tag = list(),
    feature = list(),
    feature_class = list()
){
    iatlas.app::perform_api_query(
        "features_values_by_tag",
        list(
            dataSet = dataset,
            related = group_tag,
            feature = feature,
            featureClass = feature_class
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        dplyr::select(
            "tag",
            "features"
        ) %>%
        tidyr::unnest(cols = "features") %>%
        dplyr::select(
            "tag",
            "sample",
            "feature_name" = "name",
            "feature_display" = "display",
            "feature_order" = "order",
            "feature_value" = "value"
        )
}

# features_by_class -----------------------------------------------------------

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
        dplyr::as_tibble() %>%
        tidyr::unnest(cols = c("features"))
}

query_samples_to_features <- function(features = list()){
    iatlas.app::perform_api_query(
        "samples_to_features",
        list(features = features)
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()
}

query_samples_to_feature <- function(feature){
    iatlas.app::perform_api_query(
        "samples_to_feature",
        list(feature = feature)
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()
}

query_immunomodulators <- function(){
    iatlas.app::perform_api_query(
        "immunomodulators",
        list(geneType = "immunomodulator")
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        dplyr::select(
            "entrez",
            "hgnc",
            "friendly_name" = "friendlyName",
            "description",
            "gene_family" = "geneFamily",
            "gene_function" = "geneFunction",
            "immune_checkpoint" = "immuneCheckpoint",
            "super_category" = "superCategory",
            "publications"
        )
}







# datasets ---------------------------------------------------------------------

query_datasets <- function(){
    iatlas.app::perform_api_query(
        "datasets",
        list(
            dataSet = list()
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        dplyr::select("display", "name")
}

query_dataset_samples <- function(dataset){
    iatlas.app::perform_api_query(
        "dataset_samples",
        list(
            dataSet = dataset
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        tidyr::unnest(cols = "samples")
}

