with_test_api_env({

    sample_tbl <- iatlas.app::query_cohort_selector(
        "TCGA",
        "Immune_Subtype"
    ) %>%
        dplyr::select("sample", "group" = "name")

    survival_tbl <- iatlas.app::build_survival_value_tbl(
        sample_tbl, "OS_time", "OS"
    )

    feature_tbl <- iatlas.app::query_features_values_by_tag(
        "TCGA",
        "Immune_Subtype",
        feature_class = "DNA Alteration"
    ) %>%
        dplyr::rename("group" = "tag") %>%
        dplyr::filter(sample %in% sample_tbl$sample)

    heatmap_tbl <- dplyr::inner_join(
        dplyr::select(survival_tbl, -"group"),
        feature_tbl,
        by = "sample"
    )

    heatmap_matrix <- build_co_heatmap_matrix(heatmap_tbl)


    test_that("Build Survival Values Tibble", {
        expect_named(survival_tbl, c("sample", "group", "time", "status"))
    })

    test_that("Build Heatmap Matrix", {
        expect_type(heatmap_matrix, "double")
        expect_equal(
            rownames(heatmap_matrix),
            c(
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
        )
        expect_equal(
            colnames(heatmap_matrix),
            c("C1", "C2", "C3", "C4", "C5", "C6")
        )
    })

})
