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
    })

    test_that("Build Driver Violin Tibble", {
        result1 <- build_driver_violin_tbl(1, 1, 1, 1)
        expect_named(result1, c("x", "y"))
    })

    test_that("Build Multivariate Driver Response Tibble", {
        result1 <- build_md_response_tbl(1)
        expect_named(result1, c("response", "sample_id")
        )
    })

    test_that("Build Multivariate Driver Status Tibble", {
        result1 <- build_md_status_tbl()
        expect_named(
            result1,
            c("gene_id", "sample_id", "mutation_code_id", "status",  "gene",
              "mutation_code"
            )
        )
    })

    test_that("Build Multivariate Driver Combined Tibble", {
        result1 <- build_md_combined_tbl(
            build_md_response_tbl(1),
            build_md_status_tbl(),
            dplyr::tibble(sample_id = 1:3, group = rep("G1", 3)),
            "By group"
        )
        result2 <- build_md_combined_tbl(
            build_md_response_tbl(1),
            build_md_status_tbl(),
            dplyr::tibble(sample_id = 1:3, group = rep("G1", 3)),
            "Across groups"
        )
        expect_named(
            result1,
            c("response", "gene_id", "mutation_code_id", "status",  "gene",
              "mutation_code", "group", "label"
            )
        )
        expect_named(
            result2,
            c("response", "gene_id", "mutation_code_id", "status",  "gene",
              "mutation_code", "label"
            )
        )
    })

    test_that("Filter Multivariate Driver Labels", {
        tbl <- dplyr::tribble(
            ~label, ~status,
            "l1",   "Wt",
            "l1",   "Mut",
            "l2",   "Wt",
            "l2",   "Wt",
            "l2",   "Wt",
        )
        expect_equal(filter_md_labels(tbl, 0, 0), c("l1", "l2"))
        expect_equal(filter_md_labels(tbl, 1, 1), "l1")
        expect_equal(filter_md_labels(tbl, 2, 2), character(0))
    })

    test_that("Build Multivariate Driver Pvalue Tibble", {
        tbl <- dplyr::tribble(
            ~label, ~response, ~status,
            "l1",   1,         "Wt",
            "l1",   .9,        "Wt",
            "l1",   .8,        "Mut",
            "l1",   .7,        "Mut",
        )
        result1 <- build_md_pvalue_tbl(tbl, "response ~ status")
        expect_named(result1,  c("label", "p_value", "log10_p_value")
        )
    })

    test_that("Calculate Linear Model Pvalue", {
        tbl <- dplyr::tribble(
            ~label, ~response, ~status,
            "l1",   1,         "Wt",
            "l1",   .9,        "Wt",
            "l1",   .8,        "Mut",
            "l1",   .7,        "Mut",
        )
        result1 <- calculate_lm_pvalue(tbl, "response ~ status", "statusWt")
        expect_type(result1, "double")
    })

    test_that("Build Multivariate Driver Effect Size Tibble", {
        tbl <- dplyr::tribble(
            ~label, ~response, ~status,
            "l1",   1,         "Wt",
            "l1",   .9,        "Wt",
            "l1",   .8,        "Mut",
            "l1",   .7,        "Mut",
        )
        result1 <- build_md_effect_size_tbl(tbl)
        expect_named(result1,  c("label", "fold_change", "log10_fold_change"))
    })

    test_that("Get Effect Size From Tibble", {
        tbl <- dplyr::tibble(g1 = list(2:4), g2 = list(1:11))
        expect_equal(get_effect_size_from_tbl(tbl), 1/2)
    })

    test_that("Calculate Ratio Effect Size", {
        expect_equal(calculate_ratio_effect_size(2:4, 1:11), 1/2)
        expect_true(is.na(calculate_ratio_effect_size(-3:1, 1:11)))
        expect_true(is.na(calculate_ratio_effect_size(1:3, 0)))
    })

    test_that("Build Multivariate Driver Results Tibble", {
        tbl <- dplyr::tribble(
            ~label, ~response, ~status,
            "l1",   1,         "Wt",
            "l1",   .9,        "Wt",
            "l1",   .8,        "Mut",
            "l1",   .7,        "Mut",
        )
        pvalue_tbl      <- build_md_pvalue_tbl(tbl, "response ~ status")
        effect_size_tbl <- build_md_effect_size_tbl(tbl)
        combined_tbl    <- build_md_combined_tbl(
            build_md_response_tbl(1),
            build_md_status_tbl(),
            dplyr::tibble(sample_id = c(
                26, 31, 90, 91, 92), group = rep("G1", 5)
            ),
            "By group"
        )
        result1 <-  build_md_results_tbl(
            combined_tbl,
            pvalue_tbl,
            effect_size_tbl
        )
        expect_named(
            result1,
            c("gene_id", "mutation_code_id",  "gene", "mutation_code", "group",
              "label", "p_value", "log10_p_value", "fold_change",
              "log10_fold_change")
        )
    })

    test_that("Build Multivariate Driver Violin Tibble", {
        tbl <- dplyr::tribble(
            ~label, ~response, ~status,
            "l1",   1,         "Wt",
            "l1",   .9,        "Wt",
            "l1",   .8,        "Mut",
            "l1",   .7,        "Mut",
            "l2",   100,       "Wt",
            "l2",   110,       "Mut",
            "l2",   90,        "Mut",
            "l2",   70,        "Mut",
            "l2",   60,        "Mut",
        )
        result1 <- build_md_driver_violin_tbl(tbl, "l1")
        result2 <- build_md_driver_violin_tbl(tbl, "l2")
        expect_named(result1, c("x", "y"))
        expect_named(result2, c("x", "y"))
        expect_equal(nrow(result1), 4L)
        expect_equal(nrow(result2), 5L)
    })

    test_that("Create Multivariate Driver Violin Plot Title", {
        tbl <- dplyr::tibble(
            p_value = 0.000111,
            log10_fold_change = 2.111111,
            group = "G1"
        )
        expect_equal(
            create_md_violin_plot_title(tbl, "Across groups"),
            "P-value: 1e-04 ; Log10(Fold Change): 2.1111"
        )
        expect_equal(
            create_md_violin_plot_title(tbl, "By group"),
            "Group: G1 ; P-value: 1e-04 ; Log10(Fold Change): 2.1111"
        )
    })
})
