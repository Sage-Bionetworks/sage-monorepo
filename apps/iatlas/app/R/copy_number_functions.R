get_cnv_group_tbl <- function(group){
    paste0(
        "SELECT name, id FROM tags WHERE id IN ",
        "(SELECT tag_id FROM tags_to_tags ",
        "WHERE related_tag_id IN ",
        "(SELECT id FROM tags where display = '", group, "'))"
    ) %>%
        perform_query()
}

get_cnv_group_list <- function(tbl){
    tbl %>%
        tibble::deframe(.) %>%
        c("All" = 0, .)
}

build_cnv_im_tbl <- function(){
    paste0(
        "SELECT id, ",
        create_id_to_gene_family_subquery(),
        " FROM genes a WHERE id IN ",
        "(SELECT gene_id FROM genes_to_types WHERE type_id IN ",
        "(SELECT id FROM gene_types WHERE name = 'immunomodulator'))"
    ) %>%
        perform_query()
}

build_cnv_geneset_tbl <- function(im_tbl){
    im_tbl %>%
        dplyr::select(.data$gene_family) %>%
        tidyr::drop_na() %>%
        dplyr::distinct() %>%
        dplyr::mutate(name = paste0('<geneset> ', .data$gene_family)) %>%
        dplyr::mutate(id = -1:-(nrow(.)))
}

# TODO: Not all gene ids are in gene table
# TODO: add dataset as parameter
build_cnv_gene_tbl <- function(){
    paste0(
        "SELECT hgnc, id FROM genes WHERE id IN ",
        "(SELECT gene_id FROM copy_number_results)"
    ) %>%
        perform_query()
}

get_cnv_gene_list <- function(geneset_tbl, gene_tbl){
    dplyr::bind_rows(
        dplyr::tibble(name = "All", id = 0),
        dplyr::select(geneset_tbl, .data$name, .data$id),
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

# TODO: add dataset as parameter
# build_cnv_result_tbl <- function(tag_ids){
#     query <- paste0(
        #         "SELECT direction, mean_normal, mean_cnv, log10_p_value, t_stat, ",
        #         create_id_to_hgnc_subquery(),
        #         ", ",
        #         create_id_to_feature_display_subquery(),
        #         ", ",
        #         create_id_to_tag_name_subquery(),
        #         " FROM copy_number_results a WHERE "
        #     )
# }



# TODO: add dataset as parameter
# build_cnv_result_tbl <- function(tag_ids,  gene_ids, feature_id, direction){
#     query <- paste0(
#         "SELECT direction, mean_normal, mean_cnv, log10_p_value, t_stat, ",
#         create_id_to_hgnc_subquery(),
#         ", ",
#         create_id_to_feature_display_subquery(),
#         ", ",
#         create_id_to_tag_name_subquery(),
#         " FROM copy_number_results a WHERE "
#     )
#     filters <- list()
#     print("test5")
#     if (tag_ids[[1]] != 0) {
#         filters$tag_filter <- paste0(
#             "tag_id IN (", numeric_values_to_query_list(tag_ids), ")"
#         )
#     }
#     print("test6")
#     filters$feature_filter <- paste0(
#         "feature_id = ", feature_id
#     )
#     print("test7")
#     if (gene_ids[[1]] != 0) {
#         filters$gene_filter <- paste0(
#             "gene_id IN (", numeric_values_to_query_list(gene_ids), ")"
#         )
#     }
#     if (direction != "All") {
#         filters$direction_filter <- paste0(
#             "direction = '", direction, "'"
#         )
#     }
#     print("test8")
#
#     filters %>%
#         print() %>%
#         paste0(collapse = " AND ") %>%
#         print() %>%
#         paste0(query, ., " LIMIT 10000") %>%
#         print() %>%
#         perform_query() %>%
#         print() %>%
#         dplyr::mutate(mean_diff = .data$mean_normal - .data$mean_cnv)
# }

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









