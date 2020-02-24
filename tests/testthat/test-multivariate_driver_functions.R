with_test_db_env({
    test_that("Build Multivariate Driver Covariate Tibble", {
        cov_obj1 <- list(
            "categorical_covariates" = NULL, "numerical_covariates" = NULL
        )
        cov_obj2 <- list(
            "categorical_covariates" = "Immune_Subtype",
            "numerical_covariates" = NULL
        )
        cov_obj3 <- list(
            "categorical_covariates" = NULL,
            "numerical_covariates" = 1:3
        )
        cov_obj4 <- list(
            "categorical_covariates" = "Immune_Subtype",
            "numerical_covariates" = 1:3
        )
        expect_null(build_md_covariate_tbl(cov_obj1))
        expect_named(
            build_md_covariate_tbl(cov_obj2), c("sample_id", "Immune_Subtype")
        )

        expect_equal(
            length(colnames(build_md_covariate_tbl(cov_obj3))),
            4
        )
        expect_true("sample_id" %in% colnames(build_md_covariate_tbl(cov_obj3)))

        expect_equal(
            length(colnames(build_md_covariate_tbl(cov_obj4))),
            5
        )
        expect_true(all(
            c("sample_id", "Immune_Subtype") %in%
                colnames(build_md_covariate_tbl(cov_obj4))
        ))
    })

    test_that("Build Multivariate Driver Response Tibble", {
        result1 <- build_md_response_tbl(1)
        expect_named(result1, c("response", "sample_id")
        )
    })

    test_that("Build Multivariate Driver Status Tibble", {
        result1 <- build_md_status_tbl()
        expect_named(result1, c("sample_id","status", "gene", "mutation_code"))
    })

    test_that("Combine Multivariate Driver Tibbles", {
        response_tbl <- build_md_response_tbl(1)
        status_tbl   <- build_md_status_tbl()
        sample_tbl   <- dplyr::tibble(sample_id = 1:3, group = rep("G1", 3))
        cov_tbl      <- build_md_covariate_tbl(list(
            "categorical_covariates" = "Immune_Subtype",
            "numerical_covariates" = 1:3
        ))

        result1 <- combine_md_tbls(
            response_tbl, status_tbl, sample_tbl, NULL, "By group"
        )
        result2 <- combine_md_tbls(
            response_tbl, status_tbl, sample_tbl, NULL, "Across groups"
        )
        result3 <- combine_md_tbls(
            response_tbl, status_tbl, sample_tbl, cov_tbl, "By group"
        )
        result4 <- combine_md_tbls(
            response_tbl, status_tbl, sample_tbl, cov_tbl, "Across groups"
        )
        expect_named(result1, c("response", "status", "label"))
        expect_named(result2, c("response", "status", "label"))
        expect_equal(dim(result3)[2], 7)
        expect_equal(dim(result4)[2], 7)
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
            label = "G1; A:B"
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

    test_that("Create Multivariate Driver Violin Plot X Label", {
        expect_equal(
            create_md_violin_plot_x_lab("G1:M1", "Across groups"),
            "Mutation Status G1:M1"
        )
        expect_equal(
            create_md_violin_plot_x_lab("C1;G1:M1", "By group"),
            "Mutation Status G1:M1"
        )
    })
})
