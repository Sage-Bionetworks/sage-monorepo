with_test_api_env({

    cohort_obj1 <- build_cohort_object(
        filter_obj = list(
            "samples" = "TCGA" %>%
                query_dataset_samples(.) %>%
                dplyr::slice(1:50) %>%
                dplyr::pull("name")
        ),
        dataset = "TCGA",
        group_choice = "Immune_Subtype",
        group_type = "tag"
    )

    cohort_obj2 <- build_cohort_object(
        filter_obj = list(
            "samples" = "TCGA" %>%
                query_dataset_samples(.) %>%
                dplyr::slice(1:10) %>%
                dplyr::pull("name")
        ),
        dataset = "TCGA",
        group_choice = "Immune Feature Bins",
        group_type = "custom",
        feature_name = "leukocyte_fraction",
        bin_number = 2,
        feature_tbl = "TCGA" %>%
            query_features_by_class() %>%
            dplyr::select("class", "display", "name")
    )

    test_that("build_co_survival_list", {
        res1 <- build_co_survival_list(cohort_obj1$feature_tbl)
        expect_named(res1)
        expect_vector(res1)
        res2 <- build_co_survival_list(cohort_obj2$feature_tbl)
        expect_named(res2)
        expect_vector(res2)
    })

    test_that("get_co_status_feature", {
        expect_equal(get_co_status_feature("PFI_time_1"), "PFI_1")
    })

    survival_tbl1 <- build_co_survival_value_tbl(cohort_obj1, "OS_time", "OS")
    survival_tbl2 <- build_co_survival_value_tbl(cohort_obj2, "OS_time", "OS")

    test_that("Build Survival Values Tibble", {
        expect_named(survival_tbl1, c("sample", "group", "time", "status"))
        expect_named(survival_tbl2, c("sample", "group", "time", "status"))
    })

    feature_tbl1 <- build_co_feature_tbl(
        cohort_obj1, "DNA Alteration"
    )
    feature_tbl2 <- build_co_feature_tbl(
        cohort_obj2, "DNA Alteration"
    )

    test_that("build_co_feature_tbl", {
        expected_columns <- c(
            "sample",
            "feature_display",
            "feature_value",
            "feature_order"
        )
        expect_named(feature_tbl1, expected_columns)
        expect_named(feature_tbl2, expected_columns)
    })

    heatmap_tbl1 <- build_co_heatmap_tbl(
        survival_tbl1, feature_tbl1, cohort_obj1$sample_tbl
    )
    heatmap_tbl2 <- build_co_heatmap_tbl(
        survival_tbl2, feature_tbl2, cohort_obj2$sample_tbl
    )

    test_that("build_co_heatmap_tbl", {
        expected_columns <- c(
            "sample",
            "group",
            "time",
            "status",
            "feature_display",
            "feature_value",
            "feature_order"
        )
        expect_named(heatmap_tbl1, expected_columns)
        expect_named(heatmap_tbl2, expected_columns)
    })

    heatmap_matrix1 <- build_co_heatmap_matrix(heatmap_tbl1)

    test_that("Build Heatmap Matrix", {
        expect_type(heatmap_matrix1, "double")
        expected_rownames <- c(
            "Nonsilent Mutation Rate",
            "SNV Neoantigen",
            "Indel Neoantigen",
            "Intratumor Heterogeneity",
            "Aneuploidy Score",
            "Number of Segments",
            "Fraction Altered",
            "Homologous Recombination Deficiency",
            "Number of Segments with LOH",
            "Fraction of Segments with LOH",
            "Silent Mutation Rate"
        )
        expect_equal(rownames(heatmap_matrix1), expected_rownames)

        expected_colnames <- c("C1", "C2", "C3", "C4", "C5", "C6")
        expect_equal(colnames(heatmap_matrix1), expected_colnames)
    })

})
