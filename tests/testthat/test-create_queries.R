test_that("Create Build Get Gene Expression Table By Gene IDs Query", {
    expect_equal(
        create_build_get_gene_expression_tbl_by_gene_ids_query(1:5),
        paste0(
            "SELECT gene_id, sample_id, rna_seq_expr FROM genes_to_samples ",
            "WHERE gene_id IN (1, 2, 3, 4, 5)"
        )
    )
})

test_that("Create Get Sample IDs From Parent Tag Display Query", {
    expect_equal(
        create_get_sample_ids_from_parent_tag_display_query("Immune Subtype"),
        paste0(
            "SELECT sample_id FROM samples_to_tags WHERE tag_id IN (",
            "SELECT tag_id FROM tags_to_tags WHERE related_tag_id IN (",
            "SELECT id FROM tags WHERE display IN ('Immune Subtype')))"
        )
    )
})

test_that("Create Combined Feature Values Query From Class Ids", {
    expect_equal(
        create_combined_feature_values_query_from_class_ids(1),
        paste0(
            "SELECT * FROM (SELECT * FROM features_to_samples) a ",
            "INNER JOIN (",
            "SELECT * FROM ",
            "features WHERE class_id IN (1)) b ",
            "ON a.feature_id = b.id"
        )
    )
})

test_that("Create Feature Value Query", {
    expect_equal(
        create_feature_value_query(1),
        paste0(
            "SELECT sample_id, feature_id, value FROM features_to_samples ",
            "WHERE feature_id IN (1) AND value IS NOT NULL"
        )
    )
    expect_equal(
        create_feature_value_query("1"),
        paste0(
            "SELECT sample_id, feature_id, value FROM features_to_samples ",
            "WHERE feature_id IN (1) AND value IS NOT NULL"
        )
    )
    expect_equal(
        create_feature_value_query("1,2,3"),
        paste0(
            "SELECT sample_id, feature_id, value FROM features_to_samples ",
            "WHERE feature_id IN (1,2,3) AND value IS NOT NULL"
        )
    )
})

test_that("Create Parent Group Query From Id", {
    expect_equal(
        create_parent_group_query_from_id(1),
        paste0(
            "SELECT * FROM tags WHERE id IN (",
            "SELECT tag_id FROM tags_to_tags WHERE related_tag_id IN (1))"
        )
    )
})

test_that("Create Parent Group Query From Display", {
    expect_equal(
        create_parent_group_query_from_display("Immune Subtype"),
        paste0(
            "SELECT * FROM tags WHERE id IN (",
            "SELECT tag_id FROM tags_to_tags WHERE related_tag_id IN (",
            "SELECT id FROM tags WHERE display IN ('Immune Subtype')))"
        )
    )
})

test_that("Create Get Genes by Type Query", {
    expect_equal(
        create_get_genes_by_type_query("driver"),
        paste0(
            "SELECT * FROM genes WHERE id IN (",
            "SELECT gene_id FROM genes_to_types WHERE type_id IN (",
            "SELECT id FROM gene_types WHERE name IN ('driver')))"
        )
    )
})

test_that("Create Translate Values Query", {
    expect_equal(
        create_translate_values_query("table", "col1", "col2", "'value'"),
        "SELECT col1 FROM table WHERE col2 IN ('value')"
    )

})

test_that("String Values to Query List", {
    translate_str_features <- function(values){
        create_translate_values_query(
            "features", "id", "display", values
        )
    }
    expect_equal(
        translate_str_features("'Leukocyte Fraction'"),
        "SELECT id FROM features WHERE display IN ('Leukocyte Fraction')"
    )
    expect_equal(
        translate_str_features("'Leukocyte Fraction', 'Stromal Fraction'"),
        paste(
            "SELECT id FROM features WHERE display IN",
            "('Leukocyte Fraction', 'Stromal Fraction')"
        )
    )
})

test_that("String Values to Query List", {
    expect_equal(
        string_values_to_query_list("Leukocyte Fraction"),
        "'Leukocyte Fraction'"
    )
    expect_equal(
        string_values_to_query_list(c("Leukocyte Fraction", "Stromal Fraction")),
        "'Leukocyte Fraction', 'Stromal Fraction'"
    )
})

test_that("Numeric Values to Query List", {
    expect_equal(numeric_values_to_query_list(1), "1")
    expect_equal(numeric_values_to_query_list(1L), "1")
    expect_equal(numeric_values_to_query_list(c(1, 2)), "1, 2")
    expect_equal(numeric_values_to_query_list(c(1, 2L)), "1, 2")
})

