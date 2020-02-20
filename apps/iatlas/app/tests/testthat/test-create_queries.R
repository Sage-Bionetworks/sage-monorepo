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

test_that("Create Build Immunomodulators Table Query", {
    expect_equal(
        create_build_immunomodulators_tbl_query(),
        paste0(
            "SELECT a.id, a.hgnc, a.entrez, a.friendly_name, a.references, ",
            "gf.name AS gene_family, sc.name as super_category, ic.name AS ",
            "immune_checkpoint, gfunc.name as gene_function FROM (",
            "SELECT * FROM genes WHERE id IN ",
            "(SELECT gene_id FROM genes_to_types WHERE type_id IN ",
            "(SELECT id FROM gene_types WHERE name IN ('immunomodulator')))) a " ,
            "LEFT JOIN gene_families gf ON a.gene_family_id = gf.id ",
            "LEFT JOIN super_categories sc ON a.super_cat_id = sc.id ",
            "LEFT JOIN immune_checkpoints ic ON a.immune_checkpoint_id = ic.id ",
            "LEFT JOIN gene_functions gfunc ON a.gene_function_id = gfunc.id"
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




test_that("create get feature display from id query", {
    expect_equal(
        create_get_feature_display_from_id_query(1),
        "SELECT display FROM features WHERE id IN (1)"
    )
})

test_that("create get feature display from id query", {
    expect_equal(
        create_get_feature_display_from_id_query(1),
        "SELECT display FROM features WHERE id IN (1)"
    )
})

test_that("Create Get Class Id from Name Query", {
    expect_equal(
        create_get_class_id_from_name_query("DNA Alteration"),
        "SELECT id FROM classes WHERE name IN ('DNA Alteration')"
    )
})

test_that("Create Get Feature Id from Display Query", {
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

