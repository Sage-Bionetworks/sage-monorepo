with_test_api_env({

    tcga_samples = "TCGA" %>%
        iatlas.app::query_dataset_samples(.) %>%
        dplyr::pull("name")

    pcawg_samples = "PCAWG" %>%
        iatlas.app::query_dataset_samples(.) %>%
        dplyr::pull("name")

    filter_obj1 <- list(
        "samples" =  tcga_samples,
        "filters" = list(
            "feature_filters" = list(),
            "group_filters" = list()
        )
    )

    filter_obj2 <- list(
        "samples" =  pcawg_samples,
        "filters" = list(
            "feature_filters" = list(),
            "group_filters" = list()
        )
    )

    tag_group_tbl <- query_dataset_tags("TCGA")
    custom_group_tbl <- build_custom_group_tbl("TCGA")
    available_groups_list <- build_cohort_group_list(
        tag_group_tbl, custom_group_tbl
    )

    mutation_tbl <- build_cohort_mutation_tbl()
    feature_bin_tbl <- "TCGA" %>%
        query_features_by_class() %>%
        dplyr::select("class", "display", "name")

    test_that("build_custom_group_tbl", {
        expect_equal(
            custom_group_tbl,
            dplyr::tribble(
                ~display,              ~name,
                "Immune Feature Bins", "Immune Feature Bins",
                "Driver Mutation",     "Driver Mutation",
            )
        )
        result <- build_custom_group_tbl("PCAWG")
        expect_equal(
            result,
            dplyr::tribble(
                ~display,              ~name,
                "Immune Feature Bins", "Immune Feature Bins",
            )
        )
    })

    test_that("build_cohort_group_list", {
        expect_equal(
            available_groups_list,
            c(
                "Immune Subtype"      = "Immune_Subtype",
                "TCGA Study"          = "TCGA_Study",
                "TCGA Subtype"        = "TCGA_Subtype",
                "Immune Feature Bins" = "Immune Feature Bins",
                "Driver Mutation"     = "Driver Mutation"
            )
        )
    })

    test_that("build_cohort_mutation_tbl", {
        expect_named(
            mutation_tbl,
            c("id", "entrez", "hgnc", "code", "mutation")
        )
    })

    # cohort object builder ---------------------------------------------------

    # tags --------------------------------------------------------------------

    test_that("build_cohort_tbl_by_tag", {
        expected_names <- c(
            "name",
            "group",
            "characteristics",
            "color",
            "sample",
            "size"
        )

        result1 <- build_cohort_tbl_by_tag(
            tcga_samples, "TCGA", "Immune_Subtype"
        )
        result2 <- build_cohort_tbl_by_tag(
            pcawg_samples, "PCAWG", "PCAWG_Study"
        )
        expect_named(result1, expected_names)
        expect_named(result2, expected_names)
    })

    test_that("Build Tag Cohort Object", {
        res1 <- build_tag_cohort_object("TCGA", tcga_samples, "Immune_Subtype")
        expect_named(
            res1,
            c(
                "sample_tbl",
                "group_type",
                "group_tbl",
                "group_name"
            )
        )
        expect_named(res1$sample_tbl, c("sample", "group"))
        expect_named(
            res1$group_tbl,
            c("name", "group", "characteristics", "color", "size")
        )
        expect_equal(res1$group_name, "Immune_Subtype")
        expect_equal(res1$group_type, "tag")
    })

    # mutation status ---------------------------------------------------------

    test_that("Create Driver Mutation Cohort Object", {
        res1 <- build_dm_cohort_object("TCGA", tcga_samples, 777, mutation_tbl)
        expect_named(
            res1,
            c("sample_tbl", "group_type", "group_tbl", "group_name")
        )
        expect_named(res1$sample_tbl, c("sample", "group"))
        expect_named(
            res1$group_tbl,
            c("group", "size", "name", "characteristics", "color")
        )
    })

    # feature bins ------------------------------------------------------------

    feature_bin_sample_tbl <- build_feature_bin_sample_tbl(
        "TCGA", tcga_samples, "leukocyte_fraction", 2L
    )

    test_that("Build Feature Bin Sample Tibble", {
        expect_named(feature_bin_sample_tbl, c("sample", "group"))
        expect_equal(length(unique(feature_bin_sample_tbl$group)), 2L)
    })

    test_that("Build Feature Bin Group Tibble", {
        res1 <- build_feature_bin_group_tbl(
            feature_bin_sample_tbl, "leukocyte_fraction"
        )
        expect_named(res1, c("group", "size", "name", "characteristics"))
    })

    test_that("Build Feature Bin Cohort Object",{
        res1 <- build_feature_bin_cohort_object(
            "TCGA", tcga_samples, "leukocyte_fraction", 2L, feature_bin_tbl
        )
        expect_named(res1$sample_tbl, c("sample", "group"))
        expect_named(
            res1$group_tbl,
            c("group", "size", "name", "characteristics", "color")
        )
    })

    # top level cohort function -----------------------------------------------

    test_that("Create Cohort Object", {
        res1 <- build_cohort_object(filter_obj1, "TCGA", "Immune_Subtype", "tag")
        res2 <- build_cohort_object(filter_obj2, "PCAWG", "PCAWG_Study", "tag")
        res3 <- build_cohort_object(
            filter_obj1,
            "TCGA",
            "Driver Mutation",
            "custom",
            mutation_id = 777L,
            mutation_tbl = mutation_tbl
        )
        res4 <- build_cohort_object(
            filter_obj1,
            "TCGA",
            "Immune Feature Bins",
            "custom",
            feature_name = "leukocyte_fraction",
            bin_number = 2L,
            feature_tbl = feature_bin_tbl
        )
        expected_obj_names <- c(
            "dataset",
            "feature_tbl",
            "filters",
            "group_name",
            "group_tbl",
            "group_type",
            "plot_colors",
            "sample_tbl"
        )

        expected_sample_names <- c("sample", "group")
        expected_group_names <- c(
            "group",
            "name",
            "characteristics",
            "size",
            "color"
        )
        expected_feature_names <- c(
            "class",
            "display",
            "method_tag",
            "name",
            "order",
            "unit"
        )

        expect_named(res1, expected_obj_names, ignore.order = T)
        expect_named(res3, expected_obj_names, ignore.order = T)
        expect_named(res2, expected_obj_names, ignore.order = T)
        expect_named(res4, expected_obj_names, ignore.order = T)
        expect_named(res1$group_tbl, expected_group_names, ignore.order = T)
        expect_named(res2$group_tbl, expected_group_names, ignore.order = T)
        expect_named(res3$group_tbl, expected_group_names, ignore.order = T)
        expect_named(res4$group_tbl, expected_group_names, ignore.order = T)
        expect_named(res1$feature_tbl, expected_feature_names, ignore.order = T)
        expect_named(res2$feature_tbl, expected_feature_names, ignore.order = T)
        expect_named(res3$feature_tbl, expected_feature_names, ignore.order = T)
        expect_named(res4$feature_tbl, expected_feature_names, ignore.order = T)
    })
})
