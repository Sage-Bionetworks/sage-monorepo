with_test_db_env({
    test_that("Build Univariate Driver Results Tibble", {
        result1 <- build_udr_results_tbl("Immune Subtype", 2, 50, 50)
        expect_named(
            result1,
            c("p_value", "fold_change", "log10_p_value", "gene_id", "tag_id",
              "log10_fold_change", "mutation_code_id", "gene", "mutation_code",
              "group", "label"
            )
        )
        # expect_identical(result1, tidyr::drop_na(result1))
    })
})
