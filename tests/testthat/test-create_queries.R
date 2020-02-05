test_that("create combined feature values query from class ids", {
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


test_that("create feature value query from class ids", {
    expect_equal(
        create_feature_value_query_from_class_ids(1),
        paste0(
            "SELECT sample_id, feature_id, value ",
            "FROM features_to_samples ",
            "WHERE feature_id IN ",
            "(SELECT id FROM features WHERE class_id IN (1))"
        )
    )
})

test_that("create feature value query from ids", {
    expect_equal(
        create_feature_value_query_from_ids(1),
        paste0(
            "SELECT sample_id, feature_id, value ",
            "FROM features_to_samples ",
            "WHERE feature_id IN (1)"
        )
    )
})

test_that("create get feature display from id query", {
    expect_equal(
        create_get_feature_display_from_id_query(1),
        "SELECT display FROM features WHERE id IN (1)"
    )
})

test_that("create get feature id from display query", {
    expect_equal(
        create_get_feature_id_from_display_query("Leukocyte Fraction"),
        "SELECT id FROM features WHERE display IN ('Leukocyte Fraction')"
    )
})

test_that("create get genes by type query", {
    expect_equal(
        create_get_genes_by_type_query("driver"),
        paste0(
            "SELECT * FROM genes WHERE id IN (",
            "SELECT gene_id FROM genes_to_types WHERE type_id IN (",
            "SELECT id FROM gene_types WHERE name IN ('driver')))"
        )
    )
})

test_that("create parent group query from display", {
    expect_equal(
        create_parent_group_query_from_display("Immune Subtype"),
        paste0(
            "SELECT * FROM tags WHERE id IN (",
            "SELECT tag_id FROM tags_to_tags WHERE related_tag_id IN (",
            "SELECT id FROM tags WHERE display IN ('Immune Subtype')))"
        )
    )
})

test_that("create parent group query from id", {
    expect_equal(
        create_parent_group_query_from_id(1),
        paste0(
            "SELECT * FROM tags WHERE id IN (",
            "SELECT tag_id FROM tags_to_tags WHERE related_tag_id IN (1))"
        )
    )
})

test_that("translate string values", {
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

test_that("translate numeric values", {
    translate_num_features <- function(values){
        create_translate_values_query(
            "features", "display", "id", values
        )
    }
    expect_equal(
        translate_num_features("1"),
        "SELECT display FROM features WHERE id IN (1)"
    )
    expect_equal(
        translate_num_features(c("1, 2")),
        "SELECT display FROM features WHERE id IN (1, 2)"
    )
})

test_that("string values to query list", {
    expect_equal(
        string_values_to_query_list("Leukocyte Fraction"),
        "'Leukocyte Fraction'"
    )
    expect_equal(
        string_values_to_query_list(c("Leukocyte Fraction", "Stromal Fraction")),
        "'Leukocyte Fraction', 'Stromal Fraction'"
    )
})

test_that("numeric values to query list", {
    expect_equal(numeric_values_to_query_list(1), "1")
    expect_equal(numeric_values_to_query_list(1L), "1")
    expect_equal(numeric_values_to_query_list(c(1, 2)), "1, 2")
    expect_equal(numeric_values_to_query_list(c(1, 2L)), "1, 2")
})

