build_results_tbl <- function(group_name, feature_id, min_wt, min_mut){

    query <- paste0(
        "SELECT dr.p_value, dr.fold_change, dr.log10_p_value, ",
        "dr.log10_fold_change, g.gene, t.group FROM ",
        paste0(
            "(",
            "SELECT * FROM driver_results ",
            "WHERE feature_id = ", 2,
            " AND n_wt >= ", 20,
            " AND n_mut >= ", 20,
            ") dr "
        ),
        "INNER JOIN ",
        "(SELECT id AS gene_id, hgnc AS gene FROM genes) g",
        "ON dr.gene_id = g.gene_id ",
        paste(
            "(",
            "SELECT id, name AS group FROM tags WHERE id IN ",
            "(SELECT id FROM tags WHERE display = 'Immune Subtype')",
            ") t"
        ),
        "INNER JOIN ",
        paste0(
            "(",
            "SELECT id, name AS group FROM tags WHERE id IN ",
            "(SELECT tag_id FROM tags_to_tags WHERE related_tag_id = ",
            "(SELECT id FROM tags WHERE display = 'Immune Subtype'))",
            ") t"
        ),
        "ON dr.tag_id = t.id ",
        "LIMIT 10"
    )

    paste(
        "SELECT * FROM mutataion_codes"
    ) %>%
        perform_query("Build Driver Results Table")


    #     dplyr::mutate(
    #         gene = dplyr::if_else(
    #             is.na(gene),
    #             "missing",
    #             gene
    #         ),
    #         row_n = 1:dplyr::n(),
    #         label = paste0(gene, ":", group)
    #     )
}

build_violin_tbl <- function(feature_id, gene_id, tag_id){
    subquery1 <- paste(
        "SELECT sample_id FROM samples_to_tags",
        "WHERE tag_id = ",
        tag_id
    )

    subquery2 <- paste(
        "SELECT sample_id, value FROM features_to_samples",
        "WHERE feature_id = ", feature_id,
        "AND sample_id IN (", subquery1, ")"
    )

    subquery3 <- paste(
        "SELECT sample_id, status FROM genes_to_samples",
        "WHERE gene_id  = ", gene_id,
        "AND sample_id IN (", subquery1, ")"
    )

    query <- paste(
        "SELECT f.value, g.status FROM",
        "(", subquery2, ") f",
        "INNER JOIN",
        "(", subquery3, ") g",
        "ON f.sample_id = g.sample_id"
    ) %>%
        dplyr::sql() %>%
        .GlobalEnv$perform_query("build univariate driver violin table") %>%
        dplyr::select(x = status, y = value)
}








