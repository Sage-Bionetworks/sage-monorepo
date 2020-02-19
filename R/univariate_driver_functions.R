#' Build Univariate Driver Results Tibble
#'
#' @param group_name A string in the name column of the tags table
#' @param feature_id An integer in the feature_id column of the driver_results
#' table
#' @param min_wt An integer
#' @param min_mut An integer
#' @importFrom dplyr mutate
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_udr_results_tbl <- function(group_name, feature_id, min_wt, min_mut){
    paste0(
        "SELECT dr.p_value, dr.fold_change, dr.log10_p_value, dr.gene_id, ",
        "dr.tag_id, dr.log10_fold_change, dr.mutation_code_id, ",
        "g.gene, mc.code AS mutation_code, t.group ",
        "FROM ",
        paste0(
            "(",
            "SELECT * FROM driver_results ",
            "WHERE feature_id = ", feature_id,
            " AND n_wt >= ", min_wt,
            " AND n_mut >= ", min_mut,
            ") dr "
        ),
        "LEFT JOIN ",
        "(SELECT id AS gene_id, hgnc AS gene FROM genes) g ",
        "ON dr.gene_id = g.gene_id ",
        "LEFT JOIN ",
        "mutation_codes mc ",
        "ON dr.mutation_code_id = mc.id ",
        "INNER JOIN ",
        paste0(
            "(",
            "SELECT id AS tag_id, name AS group FROM tags WHERE id IN ",
            "(SELECT tag_id FROM tags_to_tags WHERE related_tag_id = ",
            "(SELECT id FROM tags WHERE display = '",
            group_name,
            "'))",
            ") t "
        ),
        "ON dr.tag_id = t.tag_id "
    ) %>%
        perform_query("Build Univariate Driver Results Tibble") %>%
        dplyr::mutate(label = paste0(
            .data$group, "; ", .data$gene, ":", .data$mutation_code
        ))
}

#' Build Univariate Driver Violin Tibble
#'
#' @param feature_id An integer in the features_to_samples table
#' @param gene_id An interger in the genes_samples_mutations table
#' @param tag_id An integer in the samples_to_tags table
#' @param mutation_id An integer in the genes_samples_mutations table
#' @importFrom dplyr select
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_udr_violin_tbl <- function(feature_id, gene_id, tag_id, mutation_id){

    subquery1 <- paste(
        "SELECT sample_id FROM samples_to_tags",
        "WHERE tag_id = ",
        tag_id
    )

    subquery2 <- paste0(
        "SELECT sample_id, value FROM features_to_samples ",
        "WHERE feature_id = ", feature_id, " ",
        "AND sample_id IN (", subquery1, ")"
    )

    subquery3 <- paste0(
        "SELECT sample_id, status FROM genes_samples_mutations ",
        "WHERE gene_id  = ", gene_id, " ",
        "AND mutation_code_id = ", mutation_id, " ",
        "AND sample_id IN (", subquery1, ")"
    )

    paste(
        "SELECT f.value, g.status FROM",
        "(", subquery2, ") f",
        "INNER JOIN",
        "(", subquery3, ") g",
        "ON f.sample_id = g.sample_id"
    ) %>%
        perform_query("build univariate driver violin table") %>%
        dplyr::select(x = .data$status, y = .data$value)
}








