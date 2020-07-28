# copy number results ---------------------------------------------------------

query_copy_number_results <- function(
    datasets = list(),
    tags = list(),
    genes = list(),
    features = list(),
    direction = list()
){
    perform_api_query(
        "copy_number_results",
        list(
            "dataSet" = datasets,
            "tag" = tags,
            "entrez" = genes,
            "feature" = features,
            "direction" = direction
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::bind_cols(
            "feature_name" = .$feature$display,
            "hgnc"         = .$gene$hgnc,
            "tag_name"     = .$tag$name
        ) %>%
        dplyr::as_tibble() %>%
        dplyr::select(
            "feature" = "feature_name",
            "tag"     = "tag_name",
            "hgnc",
            "direction",
            "p_value" = "pValue",
            "log10_p_value" = "log10PValue",
            "mean_cnv"  = "meanCnv",
            "mean_normal" = "meanNormal",
            "t_stat" = "tStat"
        )
}

# datasets --------------------------------------------------------------------

query_datasets <- function(){
    perform_api_query(
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
    perform_api_query(
        "dataset_samples",
        list(
            dataSet = dataset
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        tidyr::unnest(cols = "samples", keep_empty = T)
}

# features --------------------------------------------------------------------

query_feature_values <- function(
    dataset = list(),
    group_tag = list(),
    feature = list(),
    class = list()
){
    perform_api_query(
        "feature_values",
        list(
            dataSet = dataset,
            related = group_tag,
            feature = feature,
            featureClass = class
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        dplyr::select("name", "display", "sample", "value", "order")
}

query_features_range <- function(
    features = NA,
    datasets = NA,
    parent_tags = NA,
    tags = NA,
    feature_classes = NA,
    samples = NA
){
    tbl <- perform_api_query(
        "features_range",
        list(
            dataSet = datasets,
            related = parent_tags,
            tag = tags,
            feature = features,
            featureClass = feature_classes,
            sample = samples
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()

    if(nrow(tbl) == 0) {
        tbl <- dplyr::tibble(
            "name" = character(),
            "display" = character(),
            "value_min" = double(),
            "value_max" = double()
        )
    } else {
        tbl <- tbl %>%
            dplyr::select(
                "name",
                "display",
                "value_min" = "valueMin",
                "value_max" = "valueMax"
            ) %>%
            dplyr::distinct()
    }
    return(tbl)
}

# features_by_tag --------------------------------------------------------------

query_feature_values_by_tag <- function(
    dataset = list(),
    group_tag = list(),
    feature = list()
){
    perform_api_query(
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
    perform_api_query(
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
        tidyr::unnest(cols = "features", keep_empty = T) %>%
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
    perform_api_query(
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
        tidyr::unnest(cols = c("features"), keep_empty = T) %>%
        dplyr::select(
            "class",
            "display",
            "name",
            "order",
            "unit",
            "method_tag" = "methodTag"
        )
}

query_samples_to_features <- function(features = list()){
    perform_api_query(
        "samples_to_features",
        list(features = features)
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()
}

query_samples_to_feature <- function(feature){
    perform_api_query(
        "samples_to_feature",
        list(feature = feature)
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()
}

# genes -----------------------------------------------------------------------

query_genes <- function(
    type = list(),
    entrez = list()
){
    perform_api_query(
        "genes",
        list(
            "geneType" = type,
            "entrez" = entrez
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::arrange(.data$entrez) %>%
        dplyr::as_tibble() %>%
        dplyr::select("hgnc", "entrez") %>%
        dplyr::arrange(.data$hgnc)
}


query_immunomodulators <- function(
    type = "immunomodulator",
    .entrez = list()
){
    perform_api_query(
        "immunomodulators",
        list(
            geneType = type,
            entrez = .entrez
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::arrange(.data$entrez) %>%
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

query_io_targets <- function(
    type = "io_target",
    .entrez = list()
){
    perform_api_query(
        "io_targets",
        list(
            geneType = type,
            entrez = .entrez
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::arrange(.data$entrez) %>%
        dplyr::as_tibble() %>%
        dplyr::select(
            "entrez",
            "hgnc",
            "description",
            "io_landscape_name" = "ioLandscapeName",
            "pathway",
            "therapy_type" = "therapyType"
        )
}

query_expression_by_genes <- function(type = NA, entrez = NA, sample = NA){
    result <-
        perform_api_query(
            "expression_by_genes",
            list(
                "geneType" = type,
                "entrez" = entrez,
                "sample" = sample
            )
        ) %>%
        purrr::pluck(1)

    if(is.null(result)) {
        tbl <- dplyr::tibble(
            "sample" = character(),
            "entrez" = character(),
            "hgnc" = character(),
            "rna_seq_expr" = double()
        )
    } else {
        tbl <- result %>%
            dplyr::as_tibble() %>%
            tidyr::unnest("samples") %>%
            dplyr::arrange(.data$name) %>%
            dplyr::select(
                "sample" = "name",
                "entrez",
                "hgnc",
                "rna_seq_expr" = "rnaSeqExpr"
            )
    }
    return(tbl)
}

# gene types ------------------------------------------------------------------

query_gene_types <- function(name = list()){
    perform_api_query(
        "gene_types",
        list("name" = name)
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        dplyr::select("display", "name") %>%
        dplyr::arrange(.data$display)
}

query_genes_by_gene_type <- function(name = list()){
    perform_api_query(
        "genes_by_gene_type",
        list("name" = name)
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        tidyr::unnest(cols = "genes") %>%
        dplyr::select(
            "entrez",
            "hgnc",
            "gene_type_name" = "name",
            "gene_type_display" = "display",
        )
}

# mutations -------------------------------------------------------------------

query_mutations <- function(
    entrez = list(),
    code = list(),
    type = list()
){
    perform_api_query(
        "mutations",
        list(
            entrez = entrez,
            mutationCode = code,
            mutationType = type
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::bind_cols(
            "entrez" = .$gene$entrez,
            "hgnc"   = .$gene$hgnc
        ) %>%
        dplyr::as_tibble() %>%
        dplyr::select(
            "id",
            "entrez",
            "hgnc",
            "code" = "mutationCode"
        )
}

# related ---------------------------------------------------------------------

query_dataset_tags <- function(dataset){
    perform_api_query(
        "dataset_tags",
        list(
            dataSet = dataset,
            related = list()
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble() %>%
        tidyr::unnest(cols = c("related"), keep_empty = T) %>%
        dplyr::select("display", "name") %>%
        dplyr::arrange(.data$name)
}

# samples by mutation status --------------------------------------------------

query_samples_by_mutation_status <- function(
    ids = NA,
    status = NA,
    samples = NA
){
    tbl <-
        perform_api_query(
            "samples_by_mutation_status",
            list(
                "mutationId" = ids,
                "mutationStatus"= status,
                "sample" = samples
            )
        ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()
    if(nrow(tbl) == 0) {
        tbl <- dplyr::tibble("sample" = character(), "status" = character())
    } else {
        tbl <- tbl %>%
            tidyr::unnest("samples") %>%
            dplyr::select("sample" = "name", "status")
    }


}
# samples by tag --------------------------------------------------------------

query_tag_samples <- function(
    datasets = NA,
    parent_tags = NA,
    tags = NA,
    features = NA,
    feature_classes = NA,
    sample_names = NA,
    patients = NA
){
    tbl <-
        perform_api_query(
            "tag_samples",
            list(
                "dataSet" = datasets,
                "related" = parent_tags,
                "tag" = tags,
                "feature" = features,
                "featureClass" = feature_classes,
                "name" = sample_names,
                "patient" = patients
            )
        ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()
    if(nrow(tbl) == 0) {
        tbl <- dplyr::tibble("sample" = character())
    } else {
        tbl <- tbl %>%
            tidyr::unnest("samples") %>%
            dplyr::select("sample" = "name")
    }
    return(tbl)

}

# tags ------------------------------------------------------------------------

query_tags <- function(dataset, parent_tag){
    perform_api_query(
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
    perform_api_query(
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
        tidyr::unnest(cols = c("samples"), keep_empty = T) %>%
        dplyr::select(
            "name",
            "display",
            "characteristics",
            "color",
            "size" = "sampleCount",
            "sample" = "samples"
        )
}










