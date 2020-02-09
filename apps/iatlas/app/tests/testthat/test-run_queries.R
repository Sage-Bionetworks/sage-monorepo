



test_that("Get Feature Values Tibble From IDs", {
    result1 <- get_feature_values_from_ids(1:5)
    expect_named(result1, c("sample_id", "feature_id", "value"))
    expect_true(all(result1$feature_id %in% 1:5))
})



test_that("Create Class List", {
    result1 <- create_class_list()
    expect_type(result1, "integer")
    expect_type(names(result1), "character")
})

test_that("Build Immunomodulators Table", {
    result1 <- build_immunomodultors_tbl()
    expect_named(
        result1,
        c("id", "hgnc", "entrez", "friendly_name", "references", "gene_family",
          "super_category", "immune_checkpoint", "gene_function"
         )
    )
})

test_that("Build Gene Expression Table by Gene IDs", {
    result1 <- build_gene_expression_tbl_by_gene_ids(1:5)
    expect_named(result1, c("gene_id", "sample_id", "rna_seq_expr"))
    expect_true(all(result1$gene_id %in% 1:5))
})

test_that("Get Sample IDs from Dataset", {
    result1 <- get_sample_ids_from_dataset("TCGA")
    expect_type(result1, "integer")
    result2 <- get_sample_ids_from_dataset("Not a dataset")
    expect_length(result2, 0)
    expect_type(result2, "integer")
    result3 <- get_sample_ids_from_dataset("PCAWG")
    expect_type(result3, "integer")
    result4 <- get_sample_ids_from_dataset("Immune Subtype")
    expect_type(result4, "integer")
})




test_that("Build Cohort Table By Group", {
    expect_named(
        build_cohort_tbl_by_group(c(1:10), "Immune Subtype"),
        c("sample_id", "group", "name", "characteristics", "color")
    )
})

test_that("Build Cohort Table By Feature ID", {
    expect_named(
        build_cohort_tbl_by_feature_id(c(1:10), 1),
        c("sample_id", "value")
    )
})

test_that("Build Feature Value Table", {
    result1 <- build_feature_value_tbl(1:5)
    expect_named(result1, c("sample_id", "feature_id", "value"))
    expect_true(all(result1$feature_id %in% 1:5))
})

# test_that("Build Driver Results Table", {
#     result1 <- build_driver_results_tbl("Immune Subtype", 2, 30, 30)
#     expect_named(
#         result1,
#         c("p_value", "fold_change", "log10_p_value", "log10_fold_change",
#           "gene", "gene_id", "group", "tag_id")
#     )
# })


test_that("Get HGNC Symbol from Gene ID", {
    result1 <- get_gene_hgnc_from_id(1L)
    expect_length(result1, 1)
    expect_type(result1, "character")
    expect_error(
        get_gene_hgnc_from_id(-1L),
        regexp = "id not greater than 0",
        fixed = T
    )
})

test_that("Get Gene ID from HGNC Symbol", {
    result1 <- get_gene_id_from_hgnc("A2M")
    expect_length(result1, 1)
    expect_type(result1, "integer")
    expect_error(
        get_gene_id_from_hgnc("Not a gene"),
        regexp = "length(id) not equal to 1",
        fixed = T
    )
})

test_that("Get Class ID from Class Name", {
    result1 <- get_class_id_from_name("DNA Alteration")
    expect_length(result1, 1)
    expect_type(result1, "integer")
    expect_error(
        get_class_id_from_name("Not a class"),
        regexp = "length(id) not equal to 1",
        fixed = T
    )
})

test_that("Get Feature Display Name From ID", {
    result1 <- get_feature_display_from_id(1L)
    expect_length(result1, 1)
    expect_type(result1, "character")
    expect_error(
        get_feature_display_from_id(-1L),
        regexp = "id not greater than 0",
        fixed = T
    )
    expect_error(
        get_feature_display_from_id(1),
        regexp = "id is not an integer vector",
        fixed = T
    )
})

test_that("Get Feature ID From Display Name", {
    result1 <- get_feature_id_from_display("Leukocyte Fraction")
    expect_length(result1, 1)
    expect_type(result1, "integer")
    expect_error(
        get_feature_id_from_display("Not a feature"),
        regexp = "length(id) not equal to 1",
        fixed = T
    )
    expect_error(
        get_feature_id_from_display(c("B Cells Memory", "Leukocyte Fraction")),
        regexp = "length(display) not equal to 1",
        fixed = T
    )
    expect_error(
        get_feature_id_from_display("B Cells"),
        regexp = "length(id) not equal to 1",
        fixed = T
    )
})

