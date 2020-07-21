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

    response_tbl1 <- query_feature_values_with_cohort_object(
        cohort_obj1,
        "EPIC_B_Cells"
    )

    response_tbl2 <- query_feature_values_with_cohort_object(
        cohort_obj2,
        "leukocyte_fraction"
    )

    feature_tbl1  <- query_feature_values_with_cohort_object(
        cohort_obj1,
        class = "EPIC"
    )

    feature_tbl2  <- query_feature_values_with_cohort_object(
        cohort_obj2,
        class = "Overall Proportion"
    )

    sample_tbl1 <- query_cohort_selector(
        "PCAWG",
        "Immune_Subtype"
    ) %>%
        dplyr::select("sample", "group" = "name")

    sample_tbl1 <- query_cohort_selector(
        "PCAWG",
        "Immune_Subtype"
    ) %>%
        dplyr::select("sample", "group" = "name")

    value_tbl <- build_ifc_value_tbl(
        response_tbl1,
        feature_tbl1,
        sample_tbl1,
        "EPIC_B_Cells"
    )

    test_that("Build Immune Feature Correlations Value Table", {

        expect_named(
            value_tbl,
            c(
                "sample",
                "group",
                "response_value",
                "feature_value",
                "feature_name",
                "feature_order"
            ),
            ignore.order = T
        )
    })

    heatmap_matrix <- build_ifc_heatmap_matrix(value_tbl, "pearson")

    test_that("Build Immune Feature Correlations Heatmap Matrix", {
        expect_equal(
            rownames(heatmap_matrix),
            c(
                "EPIC_CAFs",
                "EPIC_CD4_T_Cells",
                "EPIC_CD8_T_Cells",
                "EPIC_Endothelial",
                "EPIC_Macrophages",
                "EPIC_NK_Cells",
                "EPIC_Other_Cells"
            )
        )
        expect_equal(colnames(heatmap_matrix), c("C1", "C2", "C3", "C4", "C6"))
    })

    test_that("Build Immune Feature Scatterplot Tibble", {
        result1 <-  build_ifc_scatterplot_tbl(
            value_tbl, "EPIC_CAFs", "C1"
        )
        expect_named(result1, c("group", "name", "label", "x", "y"))
    })
})


