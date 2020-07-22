with_test_api_env({

    tcga_samples = "TCGA" %>%
        iatlas.app::query_dataset_samples(.) %>%
        dplyr::pull("name")

    filter_obj1 <- list(
        "samples" =  tcga_samples,
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

    # colors maker ------------------------------------------------------------

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

    # tags --------------------------------------------------------------------

    test_that("build_cohort_tbl_by_tag", {
        result <- build_cohort_tbl_by_tag(
            tcga_samples, "TCGA", "Immune_Subtype"
        )
        expect_named(
            result,
            c(
                "name",
                "group",
                "characteristics",
                "color",
                "sample",
                "size"
            )
        )
    })

    test_that("Build Tag Cohort Object", {
        res1 <- build_tag_cohort_object(tcga_samples, "TCGA", "Immune_Subtype")
        expect_named(
            res1,
            c(
                "sample_tbl",
                "group_type",
                "group_tbl",
                "group_name",
                "plot_colors"
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

    # test_that("Get Gene From Mutation",{
    #     expect_equal(get_gene_from_mutation("ABL1:*112Qext*9"), "ABL1")
    # })
    #
    # test_that("Get Code From Mutation",{
    #     expect_equal(get_code_from_mutation("ABL1:*112Qext*9"), "*112Qext*9")
    # })
    #
    #
    # test_that("Create Driver Mutation Cohort Object", {
    #     res1 <- build_dm_cohort_object(tcga_samples, "AKT1:E17K", mutation_tbl)
    #     expect_named(
    #         res1,
    #         c("sample_tbl", "group_tbl", "group_name", "plot_colors")
    #     )
    #     expect_named(res1$sample_tbl, c("sample_id", "group"))
    #     expect_named(
    #         res1$group_tbl,
    #         c("group", "size", "name", "characteristics")
    #     )
    # })

    # feature bins ------------------------------------------------------------

    feature_bin_sample_tbl <- build_feature_bin_sample_tbl(
        "TCGA", tcga_samples, "leukocyte_fraction", 2L
    )

    test_that("Build Feature Bin Sample Tibble", {
        expect_named(feature_bin_sample_tbl, c("sample", "group"))
        expect_equal(length(unique(feature_bin_sample_tbl$group)), 2L)
    })

    test_that("Create Feature Bin Group Tibble", {
        res1 <- build_feature_bin_group_tbl(
            feature_bin_sample_tbl, "leukocyte_fraction"
        )
        expect_named(res1, c("group", "size", "name", "characteristics", "color"))
    })

    test_that("Create Feature Bin Cohort Object",{
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
        res1 <- build_cohort_object(filter_obj1, "TCGA", "Immune_Subtype")
        # TODO: fix mutation cohort object builder
        # res2 <- build_cohort_object(
        #     filter_obj1, "TCGA", "Driver Mutation", "AKT1:E17K"
        # )
        res3 <- build_cohort_object(
            filter_obj1,
            "TCGA",
            "Immune Feature Bins",
            feature_bin_name = "leukocyte_fraction",
            feature_bin_number = 2L,
            feature_bin_tbl = feature_bin_tbl
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
        expected_group_names <- c("group", "name", "characteristics", "size", "color")
        expected_feature_names <- c("class", "display", "method_tag", "name", "order", "unit")

        expect_named(res1, expected_obj_names, ignore.order = T)
        # expect_named(res2, expected_obj_names, ignore.order = T)
        expect_named(res3, expected_obj_names, ignore.order = T)
        expect_named(res1$group_tbl, expected_group_names, ignore.order = T)
        # expect_named(res2$group_tbl, expected_group_names, ignore.order = T)
        expect_named(res3$group_tbl, expected_group_names, ignore.order = T)
        expect_named(res1$feature_tbl, expected_feature_names, ignore.order = T)
        # expect_named(res2$feature_tbl, expected_feature_names, ignore.order = T)
        expect_named(res3$feature_tbl, expected_feature_names, ignore.order = T)
    })


    #
    # test_that("Build Cohort Table By Group", {
    #     expect_named(
    #         build_cohort_tbl_by_group(c(1:10), "Immune Subtype"),
    #         c("sample_id", "group", "name", "characteristics", "color")
    #     )
    # })
    #
    # test_that("Create Tag Group Tibble",{
    #     tbl1 <- dplyr::tibble(
    #         group           = c(rep("G1", 5), "G2"),
    #         name            = c(rep("N1", 5), "N2"),
    #         characteristics = c(rep("C1", 5), "G2")
    #     )
    #     res1 <- create_tag_group_tbl(tbl1)
    #     expect_named(res1, c("group", "name", "characteristics", "size"))
    #     expect_equal(res1$size, c(5,1))
    # })


    #

    #
    # test_that("Create Driver Mutation Cohort Sample Tibble",{
    #     res1 <- create_dm_cohort_sample_tbl(1:100000, 32, 40)
    #     expect_named(res1, c("sample_id", "group"))
    # })
    #
    # test_that("Create Driver Mutation Cohort Group Tibble",{
    #     tbl1 <- dplyr::tibble(group = c(rep("Wt", 5), "Mut"))
    #     res1 <- create_dm_cohort_group_tbl(tbl1, "Gene1")
    #     expect_named(res1, c("group", "size", "name", "characteristics"))
    #     expect_equal(res1$size, c(1, 5))
    # })
    #

    #

    #
    # test_that("Build Cohort Tibble By Feature ID", {
    #     res1 <- build_cohort_tbl_by_feature_id(1:10000L, 2L)
    #     expect_named(res1, c("sample_id", "value"))
    # })
    #

    #


})
