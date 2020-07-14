get_cnv_group_tbl <- function(dataset, group){
    iatlas.app::query_tags(dataset, group)
}

get_cnv_group_list <- function(tbl){
    tbl %>%
        dplyr::pull("name") %>%
        c("All", .)
}

get_cnv_im_ids <- function(){
    paste0(
        "SELECT gene_id FROM genes_to_types WHERE type_id IN ",
        "(SELECT id FROM gene_types WHERE name = 'immunomodulator')"
    ) %>%
        perform_query() %>%
        dplyr::pull(.data$gene_id)
}

# TODO: Not all gene ids are in gene table
# TODO: add dataset as parameter
build_cnv_gene_tbl <- function(){
    paste0(
        "SELECT hgnc, id FROM genes ",
        "WHERE id IN ",
        "(SELECT gene_id FROM copy_number_results) "
    ) %>%
        perform_query()
}


# TODO: add additional genesets
get_cnv_gene_list <- function(gene_tbl){
    dplyr::bind_rows(
        dplyr::tibble(name = "All", id = 0),
        dplyr::tibble(name = "Immunomodulators", id = -1),
        dplyr::rename(gene_tbl, name = .data$hgnc)
    ) %>%
        tibble::deframe(.)
}


# TODO: add dataset as parameter
build_cnv_feature_list <- function(){
    paste0(
        "SELECT ",
        create_id_to_class_subquery(),
        ", a.display, a.id AS feature ",
        "FROM features a WHERE id IN ",
        "(SELECT feature_id FROM copy_number_results)"
    ) %>%
        perform_query() %>%
        create_nested_named_list()
}


build_cnv_result_tbl <- function(tag_ids, gene_ids, feature_id, direction){
    query <- paste0(
        "SELECT direction, mean_normal, mean_cnv, log10_p_value, t_stat, ",
        create_id_to_hgnc_subquery(),
        ", ",
        create_id_to_feature_display_subquery(),
        ", ",
        create_id_to_tag_name_subquery(),
        " FROM copy_number_results a WHERE "
    )
    filters <- list()
    if (tag_ids[[1]] != 0) {
        filters$tag_filter <- paste0(
            "tag_id IN (", numeric_values_to_query_list(tag_ids), ")"
        )
    }
    filters$feature_filter <- paste0(
        "feature_id = ", feature_id
    )
    if (gene_ids[[1]] != 0) {
        filters$gene_filter <- paste0(
            "gene_id IN (", numeric_values_to_query_list(gene_ids), ")"
        )
    }
    if (direction != "All") {
        filters$direction_filter <- paste0(
            "direction = '", direction, "'"
        )
    }

    filters %>%
        paste0(collapse = " AND ") %>%
        paste0(query, .) %>%
        perform_query() %>%
        dplyr::mutate(mean_diff = .data$mean_normal - .data$mean_cnv)
}


create_cnv_results_string <- function(result_tbl){
    n_genes <- result_tbl %>%
        dplyr::pull(.data$gene) %>%
        unique() %>%
        sort() %>%
        length()
    paste0(
        "Total number of rows: ", nrow(result_tbl),
        ", Number of genes: ", n_genes
    )
}

build_cnv_response_tbl <- function(feature_id){
    paste0(
        "SELECT sample_id, value FROM features_to_samples a ",
        "WHERE feature_id IN(", feature_id, ")"
    ) %>%
        perform_query()
}

build_cnv_dt_tbl <- function(tbl){
    tbl %>%
        dplyr::select(
            Metric             = .data$display,
            Group              = .data$tag,
            Gene               = .data$gene,
            Direction          = .data$direction,
            `Mean Normal`      = .data$mean_normal,
            `Mean CNV`         = .data$mean_cnv,
            `Mean Diff`        = .data$mean_diff,
            `T stat`           = .data$t_stat,
            `Neg log10 pvalue` = .data$log10_p_value
        ) %>%
        dplyr::mutate_if(is.numeric, round, 3)
}









