with_test_api_env({

    cohort_obj1 <- list(
        "dataset" = "PCAWG",
        "group_type" = "tag",
        "group_name" = "Immune_Subtype"
    )

    cohort_obj2 <- list(
        "dataset" = "TCGA",
        "group_type" = "custom",
        "group_name" = "Immune_Feature_Bins"
    )

    response_tbl1 <- build_ifc_response_tbl(cohort_obj1, "EPIC_B_Cells")
    response_tbl2 <- build_ifc_response_tbl(cohort_obj2, "leukocyte_fraction")

    test_that("build_ifc_response_tbl", {
        expected_columns <-  c("sample", "response_name", "response_value")
        expect_named(response_tbl1, expected_columns)
        expect_named(response_tbl2, expected_columns)
    })

    feature_tbl1  <- build_ifc_feature_tbl(cohort_obj1, "EPIC")
    feature_tbl2  <- build_ifc_feature_tbl(cohort_obj2, "Overall Proportion")

    test_that("build_ifc_feature_tbl", {
        expected_columns <- c(
            "sample",
            "feature_name",
            "feature_value",
            "feature_order"
        )
        expect_named(feature_tbl1, expected_columns)
        expect_named(feature_tbl2, expected_columns)
    })

    sample_tbl1 <- query_cohort_selector("PCAWG", "Immune_Subtype") %>%
        dplyr::select("sample", "group" = "name")

    sample_tbl2 <- query_cohort_selector("TCGA", "Immune_Subtype") %>%
        dplyr::select("sample", "group" = "name")

    value_tbl1 <- build_ifc_value_tbl(response_tbl1, feature_tbl1, sample_tbl1)
    value_tbl2 <- build_ifc_value_tbl(response_tbl2, feature_tbl2, sample_tbl2)

    test_that("Build Immune Feature Correlations Value Table", {
        expected_columns <- c(
            "sample",
            "group",
            "response_value",
            "feature_value",
            "feature_name",
            "feature_order"
        )

        expect_named(value_tbl1, expected_columns)
        expect_named(value_tbl2, expected_columns)
    })

    heatmap_matrix1 <- build_ifc_heatmap_matrix(value_tbl1, "pearson")
    heatmap_matrix2 <- build_ifc_heatmap_matrix(value_tbl2, "pearson")

    test_that("Build Immune Feature Correlations Heatmap Matrix", {
        expect_equal(
            rownames(heatmap_matrix1),
            c(
                "EPIC CAFs",
                "EPIC CD4 T Cells",
                "EPIC CD8 T Cells",
                "EPIC Endothelial",
                "EPIC Macrophages",
                "EPIC NK Cells",
                "EPIC Other Cells"
            )
        )
        expect_equal(colnames(heatmap_matrix1), c("C1", "C2", "C3", "C4", "C6"))

        expect_equal(
            rownames(heatmap_matrix2),
            c("Tumor Fraction", "Stromal Fraction")
        )
        expect_equal(
            colnames(heatmap_matrix2),
            c("C1", "C2", "C3", "C4", "C5", "C6")
        )
    })

    test_that("Build Immune Feature Scatterplot Tibble", {
        result1 <-  build_ifc_scatterplot_tbl(
            value_tbl1, "EPIC CAFs", "C1"
        )
        expect_named(result1, c("label", "x", "y"))
    })
})


