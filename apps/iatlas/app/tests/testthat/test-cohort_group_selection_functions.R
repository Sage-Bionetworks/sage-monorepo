with_test_db_env({

    test_that("Get Cohort Available Groups", {
        tbl <- dplyr::tribble(
            ~group,                 ~dataset, ~type,
            "Immune Subtype",       "TCGA",   "tag",
            "Gender",               "TCGA",   "sample",
            "Immune Feature Bins",  "TCGA",    NA,
            "Driver Mutation",      "TCGA",    NA,
            "PCAWG Study",          "PCAWG",  "tag",
            "Gender",               "PCAWG",  "sample",
        )
        expect_equal(
            get_cohort_available_groups(tbl, "PCAWG"),
            c("PCAWG Study", "Gender")
        )
    })

    test_that("Build Driver Mutation Tibble", {
        expect_named(
            build_dm_tbl(),
            c("gene", "mutation_code", "mutation")
        )
    })

    test_that("Create Cohort Object", {
        filter_obj1 <- list(
            "sample_ids" = 1:10000,
            "filters" = list(
                "feature_filters" = list(),
                "group_filters" = list()
            )
        )
        res1 <- create_cohort_object(filter_obj1, "Immune Subtype", "TCGA")
        res2 <- create_cohort_object(
            filter_obj1, "Driver Mutation", "TCGA", "AKT1:E17K"
        )
        res3 <- create_cohort_object(
            filter_obj1,
            "Immune Feature Bins",
            "TCGA",
            immune_feature_bin_id = 2L,
            immune_feature_bin_number = 2L
        )
        expected_obj_names <- c(
            "sample_tbl", "group_tbl", "group_name", "plot_colors", "dataset",
            "filters"
        )
        expected_group_names <- c("group", "name", "characteristics", "size")

        expect_named(res1, expected_obj_names)
        expect_named(res2, expected_obj_names)
        expect_named(res3, expected_obj_names)
        expect_named(res1$group_tbl, expected_group_names, ignore.order = T)
        expect_named(res2$group_tbl, expected_group_names, ignore.order = T)
        expect_named(res3$group_tbl, expected_group_names, ignore.order = T)
    })

    test_that("Create Tag Cohort Object", {
        res1 <- create_tag_cohort_object(1:10000, "Immune Subtype")
        expect_named(
            res1,
            c("sample_tbl", "group_tbl", "group_name", "plot_colors")
        )
        expect_named(res1$sample_tbl, c("sample_id", "group"))
        expect_named(
            res1$group_tbl,
            c("group", "name", "characteristics", "size")
        )
        expect_equal(res1$group_name, "Immune Subtype")
    })

    test_that("Create Tag Group Tibble",{
        tbl1 <- dplyr::tibble(
            group           = c(rep("G1", 5), "G2"),
            name            = c(rep("N1", 5), "N2"),
            characteristics = c(rep("C1", 5), "G2")
        )
        res1 <- create_tag_group_tbl(tbl1)
        expect_named(res1, c("group", "name", "characteristics", "size"))
        expect_equal(res1$size, c(5,1))
    })

    test_that("Create Driver Mutation Cohort Object", {
        res1 <- create_dm_cohort_object(1:10000, "AKT1:E17K")
        expect_named(
            res1,
            c("sample_tbl", "group_tbl", "group_name", "plot_colors")
        )
        expect_named(res1$sample_tbl, c("sample_id", "group"))
        expect_named(
            res1$group_tbl,
            c("group", "size", "name", "characteristics")
        )
    })

    test_that("Get Gene From Mutation",{
        expect_equal(get_gene_from_mutation("ABL1:*112Qext*9"), "ABL1")
    })

    test_that("Get Code From Mutation",{
        expect_equal(get_code_from_mutation("ABL1:*112Qext*9"), "*112Qext*9")
    })

    test_that("Create Driver Mutation Cohort Sample Tibble",{
        res1 <- create_dm_cohort_sample_tbl(1:100000, 32, 40)
        expect_named(res1, c("sample_id", "group"))
    })

    test_that("Create Driver Mutation Cohort Group Tibble",{
        tbl1 <- dplyr::tibble(group = c(rep("Wt", 5), "Mut"))
        res1 <- create_dm_cohort_group_tbl(tbl1, "Gene1")
        expect_named(res1, c("group", "size", "name", "characteristics"))
        expect_equal(res1$size, c(1, 5))
    })

    test_that("Create Feature Bin Cohort Object",{
        res1 <- create_feature_bin_cohort_object(1:10000L, 2L, 2L)
        expect_named(res1$sample_tbl, c("sample_id", "group"))
        expect_named(
            res1$group_tbl,
            c("group", "size", "name", "characteristics")
        )
    })

    test_that("Create Feature Bin Sample Tibble", {
        res1 <- create_feature_bin_sample_tbl(1:10000L, 2L, 2L)
        expect_named(res1, c("sample_id", "group"))
        expect_equal(length(unique(res1$group)), 2L)
    })

    test_that("Create Feature Bin Group Tibble", {
        tbl1 <- create_feature_bin_sample_tbl(1:10000L, 2L, 2L)
        res1 <- create_feature_bin_group_tbl(tbl1, "feature1")
        expect_named(res1, c("group", "size", "name", "characteristics"))
    })

    test_that("Create Plot Colors List",{
        tbl1 <- dplyr::tibble(group = c("C1", "C2"), color = c("red", "blue"))
        tbl2 <- dplyr::tibble(group = c("C2", "C1"), color = NA)
        tbl3 <- dplyr::tibble(group = c("C2", "C1"), color = c("red", NA))
        tbl4 <- dplyr::tibble(group = c("C2", "C1"))
        tbl5 <- dplyr::tibble(group = c("C1", "C1"))
        res1 <- create_plot_colors_list(tbl1)
        res2 <- create_plot_colors_list(tbl2)
        res3 <- create_plot_colors_list(tbl3)
        res4 <- create_plot_colors_list(tbl4)
        res5 <- create_plot_colors_list(tbl5)
        expect_named(res1, c("C1", "C2"))
        expect_named(res2, c("C1", "C2"))
        expect_named(res3, c("C1", "C2"))
        expect_named(res4, c("C1", "C2"))
        expect_named(res5, "C1")
        expect_type(res1, "character")
        expect_type(res2, "character")
        expect_type(res3, "character")
        expect_type(res4, "character")
        expect_type(res5, "character")
        expect_equal(unname(res1), c("red", "blue"))
        expect_equal(unname(res2), c("#440154FF", "#FDE725FF"))
        expect_equal(unname(res3), c("#440154FF", "#FDE725FF"))
        expect_equal(unname(res4), c("#440154FF", "#FDE725FF"))
        expect_equal(unname(res5), "#440154FF")
    })

})
