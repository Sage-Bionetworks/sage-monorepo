# copy number results ---------------------------------------------------------



# TODO: finsh query: https://gitlab.com/cri-iatlas/iatlas-api/-/issues/14
# TODO: add gene query
# query_copy_number_results <- function(
#     datasets = NA,
#     features = NA,
#     genes = NA,
#     tags = NA,
#     direction = NA,
#     min_p_value = NA,
#     max_p_value = NA,
#     min_log_10_p_value = NA,
#     max_log_10_p_value = NA,
#     min_mean_normal = NA,
#     min_mean_cnv = NA,
#     min_t_stat = NA,
#     page = NA
# ){
#     tbl <- perform_api_query(
#         "copy_number_results",
#         list(
#             "dataSet" = datasets,
#             "feature" = features,
#             "entrez" = genes,
#             "tag" = tags,
#             "direction" = direction,
#             "minPValue" = min_p_value,
#             "maxPValue" = max_p_value,
#             "minLog10PValue" = min_log_10_p_value,
#             "maxLog10PValue" = max_log_10_p_value,
#             "minMeanNormal" = min_mean_normal,
#             "minMeanCnv" = min_mean_cnv,
#             "minTStat" = min_t_stat,
#             "page" = page
#         )
#     )
#
#     if(nrow(tbl) == 0) {
#         tbl <- dplyr::tibble(
#             "feature" = character(),
#             "gene" = character(),
#             "tag"  = character(),
#             "direction" = character(),
#             "meanNormal" = double(),
#             "meanCnv" = double(),
#             "pValue" = double(),
#             "log10PValue" = double(),
#             "tStat" = double()
#         )
#     }
#     return(tbl)
    # %>%
    #     dplyr::bind_cols(
    #         "feature_name" = .$feature$display,
    #         "hgnc"         = .$gene$hgnc,
    #         "tag_name"     = .$tag$name
    #     ) %>%
    #     dplyr::as_tibble() %>%
    #     dplyr::select(
    #         "feature" = "feature_name",
    #         "tag"     = "tag_name",
    #         "hgnc",
    #         "direction",
    #         "p_value" = "pValue",
    #         "log10_p_value" = "log10PValue",
    #         "mean_cnv"  = "meanCnv",
    #         "mean_normal" = "meanNormal",
    #         "t_stat" = "tStat"
    #     )
# }

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

query_features <- function(
    features = NA,
    datasets = NA,
    parent_tags = NA,
    tags = NA,
    feature_classes = NA,
    samples = NA,
    max_value = NA,
    min_value = NA
){
    tbl <- perform_api_query(
        "features",
        list(
            dataSet = datasets,
            related = parent_tags,
            tag = tags,
            feature = features,
            featureClass = feature_classes,
            sample = samples,
            maxValue = max_value,
            minValue = min_value
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()

    if(nrow(tbl) == 0) {
        tbl <- dplyr::tibble(
            "name" = character(),
            "display" = character(),
            "class" = character(),
            "order" = integer(),
            "unit" =  character(),
            "method_tag" = character()
        )
    } else {
        tbl <- tbl %>%
            dplyr::select(
                "name",
                "display",
                "class",
                "order",
                "unit",
                "method_tag" = "methodTag"
            )
    }
    return(tbl)
}
query_feature_values <- function(
    features = NA,
    datasets = NA,
    parent_tags = NA,
    tags = NA,
    feature_classes = NA,
    samples = NA,
    max_value = NA,
    min_value = NA
){
    tbl <- perform_api_query(
        "feature_values",
        list(
            dataSet = datasets,
            related = parent_tags,
            tag = tags,
            feature = features,
            featureClass = feature_classes,
            sample = samples,
            maxValue = max_value,
            minValue = min_value
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()

    if(nrow(tbl) == 0) {
        tbl <- dplyr::tibble(
            "sample" = character(),
            "feature_name" = character(),
            "feature_display" = character(),
            "feature_value" = double(),
            "feature_order" = integer()
        )
    } else {
        tbl <- tbl %>%
            dplyr::select(
                "feature_name" = "name",
                "feature_display" = "display",
                "feature_order" = "order",
                "samples"
            ) %>%
            tidyr::unnest(cols = "samples") %>%
            dplyr::select(
                "sample" = "name",
                "feature_name",
                "feature_display",
                "feature_value" = "value",
                "feature_order"
            )
    }
    return(tbl)
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
            )
    }
    return(tbl)
}

# features_by_tag --------------------------------------------------------------

query_feature_values_by_tag <- function(
    feature,
    datasets = NA,
    parent_tags = NA,
    tags = NA,
    samples = NA
){
    tbl <- perform_api_query(
        "feature_values_by_tag",
        list(
            dataSet = datasets,
            related = parent_tags,
            tag = tags,
            feature = feature,
            sample = samples
        )
    ) %>%
        purrr::pluck(1) %>%
        dplyr::as_tibble()

    if(nrow(tbl) == 0) {
        tbl <- dplyr::tibble(
            "tag_name" = character(),
            "tag_display"  = character(),
            "tag_color"  = character(),
            "tag_characteristics"  = character(),
            "sample" = character(),
            "value" = double()
        )
    } else {
        tbl <- tbl %>%
            dplyr::select(
                "tag_name" = "tag",
                "tag_display" = "display",
                "tag_color" = "color",
                "tag_characteristics" = "characteristics",
                "features"
            ) %>%
            tidyr::unnest(cols = "features") %>%
            tidyr::unnest(cols = "samples") %>%
            dplyr::select(
                "tag_name",
                "tag_display",
                "tag_color",
                "tag_characteristics",
                "sample" = "name",
                "value"
            )
    }
    return(tbl)
}

query_features_values_by_tag <- function(
    features = NA,
    datasets = NA,
    parent_tags = NA,
    tags = NA,
    feature_classes = NA,
    samples = NA
){
    tbl <- perform_api_query(
        "features_values_by_tag",
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
            "tag_name" = character(),
            "tag_display"  = character(),
            "tag_color"  = character(),
            "tag_characteristics"  = character(),
            "sample" = character(),
            "feature_name" = character(),
            "feature_display" = character(),
            "feature_value" = double(),
            "feature_order" = integer()
        )
    } else {
        tbl <- tbl %>%
            dplyr::select(
                "tag_name" = "tag",
                "tag_display" = "display",
                "tag_color" = "color",
                "tag_characteristics" = "characteristics",
                "features"
            ) %>%
            tidyr::unnest(cols = "features") %>%
            dplyr::select(
                "tag_name",
                "tag_display",
                "tag_color",
                "tag_characteristics",
                "feature_name" = "name",
                "feature_display" = "display",
                "feature_order" = "order",
                "samples"
            ) %>%
            tidyr::unnest(cols = "samples") %>%
            dplyr::select(
                "tag_name",
                "tag_display",
                "tag_color",
                "tag_characteristics",
                "sample" = "name",
                "feature_name",
                "feature_display",
                "feature_value" = "value",
                "feature_order"
            )
    }
    return(tbl)
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

# genes -----------------------------------------------------------------------

query_genes <- function(
    gene_types = NA,
    entrez_ids = NA,
    samples = NA
){
    result <- perform_api_query(
        "genes",
        list(
            "geneType" = gene_types,
            "entrez" = entrez_ids,
            "sample" = samples
        )
    ) %>%
        purrr::pluck(1)

    if(is.null(result)) {
        tbl <- dplyr::tibble(
            "hgnc" = character(),
            "entrez" = integer(),
            "description" = character(),
            "friendly_name" = character(),
            "io_landscape_name" = character(),
            "gene_family" = character(),
            "gene_function" = character(),
            "immune_checkpoint" = character(),
            "pathway" = character(),
            "super_category" = character()
        )
    } else {
        tbl <- result %>%
            dplyr::as_tibble() %>%
            dplyr::select(
                "hgnc",
                "entrez",
                "description",
                "friendly_name" = "friendlyName",
                "io_landscape_name" = "ioLandscapeName",
                "gene_family" = "geneFamily",
                "gene_function" = "geneFunction",
                "immune_checkpoint" = "immuneCheckpoint",
                "pathway",
                "super_category" = "superCategory"
            ) %>%
            dplyr::arrange(.data$hgnc)
    }
}


query_immunomodulators <- function(
    type = "immunomodulator",
    .entrez = list()
){
    tbl <- perform_api_query(
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
    return(tbl)
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

# nodes -----------------------------------------------------------------------

#TODO: fix query: https://gitlab.com/cri-iatlas/iatlas-api/-/issues/8
# query_node_tags <- function(
#     datasets = NA,
#     parent_tags = NA,
#     networks = NA,
#     page = NA
# ){
#     tbl <-
#         perform_api_query(
#             "node_tags",
#             list(
#                 # dataSet = datasets,
#                 # related = parent_tags,
#                 # network = networks,
#                 # page = page
#                 dataSet = "TCGA",
#                 related = c("Immune_Subtype", "TCGA_Study"),
#                 network = "extracellular_network",
#                 page = NA
#             )
#         ) %>%
#         purrr::pluck(1) %>%
#         dplyr::as_tibble()
#     if(nrow(tbl) == 0) {
#         tbl2 <- dplyr::tibble("sample" = character(), "status" = character())
#     } else {
#         tbl2 <-
#             dplyr::tibble(
#                 "node_name" = tbl$items$name,
#                 "tags" = tbl$items$tags
#             ) %>%
#             tidyr::unnest("tags", keep_empty = T) %>%
#             dplyr::select("sample" = "name", "status")
#     }
#     return(tbl2)
# }


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

query_samples_by_tag <- function(
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
            "samples_by_tag",
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
        tbl <- dplyr::tibble(
            "tag_name" = character(),
            "tag_display" = character(),
            "tag_characteristics" = character(),
            "tag_color" = character(),
            "sample" = character()
        )
    } else {
        tbl <- tbl %>%
            dplyr::select(
                "tag_name" = "tag",
                "tag_display" = "display",
                "tag_characteristics" = "characteristics",
                "tag_color" = "color",
                "samples"
            ) %>%
            tidyr::unnest("samples") %>%
            dplyr::rename("sample" = "name")
    }

}

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

query_tags <- function(
    datasets,
    parent_tags,
    tags = NA,
    features = NA,
    feature_classes = NA,
    samples = NA
){
    tbl <- perform_api_query(
        "tags",
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
            "characteristics" = character(),
            "color" = character(),
            "sample_count" = integer()
        )
    } else {
        tbl <- tbl %>%
            dplyr::select(
                "name",
                "display",
                "characteristics",
                "color",
                "sample_count" = "sampleCount"
            ) %>%
            dplyr::arrange(.data$name)
    }
    return(tbl)
}

query_cohort_selector <- function(
    datasets,
    related_tags,
    tags = NA,
    features = NA,
    feature_classes = NA,
    samples = NA
){
    tbl <- perform_api_query(
        "cohort_selection",
        list(
            dataSet = datasets,
            related = related_tags,
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
            "characteristics" = character(),
            "color" = character(),
            "size" = integer(),
            "samples" = list()
        )
    } else {
        tbl <- tbl %>%
            dplyr::select(
                "name",
                "display",
                "characteristics",
                "color",
                "size" = "sampleCount",
                "samples"
            ) %>%
            dplyr::arrange(.data$name)
    }
    return(tbl)
}










