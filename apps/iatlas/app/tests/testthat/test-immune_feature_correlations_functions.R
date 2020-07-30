with_test_api_env({

    cohort_obj1 <- build_cohort_object(
        filter_obj = list(
            "samples" = "PCAWG" %>%
                iatlas.app::query_dataset_samples(.) %>%
                dplyr::pull("name")
        ),
        dataset = "PCAWG",
        group_choice = "Immune_Subtype",
        group_type = "tag"
    )

    response_tbl1 <- build_ifc_response_tbl(cohort_obj1, "EPIC_B_Cells")

    test_that("build_ifc_response_tbl", {
        expected_columns <-  c("sample", "response_display", "response_value")
        expect_named(response_tbl1, expected_columns)
    })

    feature_tbl1  <- build_ifc_feature_tbl(cohort_obj1, "EPIC")

    test_that("build_ifc_feature_tbl", {
        expected_columns <- c(
            "sample",
            "feature_display",
            "feature_value",
            "feature_order"
        )
        expect_named(feature_tbl1, expected_columns)
    })

    value_tbl1 <- build_ifc_value_tbl(
        response_tbl1, feature_tbl1, cohort_obj1$sample_tbl
    )

    test_that("Build Immune Feature Correlations Value Table", {
        expected_columns <- c(
            "sample",
            "group",
            "response_value",
            "feature_value",
            "feature_display",
            "feature_order"
        )

        expect_named(value_tbl1, expected_columns)
    })

    heatmap_matrix1 <- build_ifc_heatmap_matrix(value_tbl1, "pearson")

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

    })

    test_that("Build Immune Feature Scatterplot Tibble", {
        result1 <-  build_ifc_scatterplot_tbl(
            value_tbl1, "EPIC CAFs", "C1"
        )
        expect_named(result1, c("label", "x", "y"))
    })
})


