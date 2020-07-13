with_test_api_env({

    test_that("Create Cohort Tag Named List", {
        result1 <- create_cohort_tag_named_list("TCGA")
        result2 <- create_cohort_tag_named_list ("PCAWG")
        expect_type(result1, "character")
        expect_type(result2, "character")
        expect_named(
            result1,
            c("Immune Subtype", "TCGA Subtype", "TCGA Study"),
            ignore.order = T
        )
        expect_named(
            result2,
            c("Immune Subtype", "PCAWG Study"),
            ignore.order = T
        )
    })

    test_that("Get Valid Group Filters", {
        expect_equal(get_valid_group_filters(list()), list())
        expect_equal(get_valid_group_filters(list(NULL)), list())
        expect_equal(
            get_valid_group_filters(list("element1" = list("ids" = 1))),
            list()
        )
        expect_equal(
            get_valid_group_filters(
                list("element1" = list("ids" = 1, "name" = "Group"))
            ),
            list(list("ids" = 1, "name" = "Group"))
        )
        expect_equal(
            get_valid_group_filters(
                list(
                    "element1" = list("ids" = 1, "name" = "Group"),
                    "element2" = list("ids" = 1)
                )
            ),
            list(list("ids" = 1, "name" = "Group"))
        )
        expect_equal(
            get_valid_group_filters(
                list(
                    "element1" = list("ids" = 1, "name" = "Group"),
                    "element2" = list("ids" = 1, "name" = "Group")
                )
            ),
            list(
                list("ids" = 1, "name" = "Group"),
                list("ids" = 1, "name" = "Group")
            )
        )
    })
#
#     test_that("Get Filtered Group Sample IDs", {
#         expect_equal(get_filtered_group_sample_ids(list(), 1:10000), 1:10000)
#         result1 <- get_filtered_group_sample_ids(
#             list(
#                 "element1" = list("ids" = 1, "name" = "Group"),
#                 "element2" = list("ids" = 1, "name" = "Group")
#             ),
#             1:10000
#         )
#         expect_type(result1, "integer")
#     })
#
#     test_that("Get Filtered Group Sample IDs By Filter", {
#         expect_type(get_filtered_group_sample_ids_by_filter(1), "integer")
#     })
#
#     test_that("Get Valid Numeric Filters", {
#         expect_equal(get_valid_numeric_filters(list()), list())
#         expect_equal(get_valid_numeric_filters(list(NULL)), list())
#         expect_equal(
#             get_valid_numeric_filters(list("element1" = list("id" = 1))),
#             list()
#         )
#         expect_equal(
#             get_valid_numeric_filters(
#                 list("element1" = list("id" = 1, "min" = 1))
#             ),
#             list()
#         )
#         expect_equal(
#             get_valid_numeric_filters(
#                 list("element1" = list("id" = 1, "max" = 1))),
#             list()
#         )
#         expect_equal(
#             get_valid_numeric_filters(
#                 list("element1" = list("id" = 1, "max" = 1, "min" = 0))
#             ),
#             list(list("id" = 1, "max" = 1, "min" = 0))
#         )
#         expect_equal(
#             get_valid_numeric_filters(
#                 list(
#                     "element1" = list("id" = 1, "max" = 1, "min" = 0),
#                     "element2" = list("id" = 1, "max" = 1)
#                 )
#             ),
#             list(list("id" = 1, "max" = 1, "min" = 0))
#         )
#         expect_equal(
#             get_valid_numeric_filters(
#                 list(
#                     "element1" = list("id" = 1, "max" = 1, "min" = 0),
#                     "element2" = list("id" = 1, "max" = 1, "min" = 0)
#                 )
#             ),
#             list(
#                 list("id" = 1, "max" = 1, "min" = 0),
#                 list("id" = 1, "max" = 1, "min" = 0)
#             )
#         )
#     })
#
#     test_that("Is Numeric Filter Valid", {
#         expect_false(is_numeric_filter_valid(NULL))
#         expect_false(is_numeric_filter_valid(list("id" = 1, "max" = 1)))
#         expect_false(
#             is_numeric_filter_valid(list("id" = 1, "min" = 0, "mx" = 1))
#         )
#     })#
    #     test_that("Create Cohorts Group Named List", {
    #         result1 <- create_cohort_group_named_list(tbl, "TCGA")
    #         result2 <- create_cohort_group_named_list(tbl, "PCAWG")
    #         expect_type(result1, "integer")
    #         expect_type(result2, "integer")
    #         expect_named(
    #             result1,
    #             c("Immune Subtype", "TCGA Subtype", "TCGA Study"),
    #             ignore.order = T
    #         )
    #         # TODO Fix this test when test database contains PCAWG Study
    #         expect_named(
    #             result2,
    #             c("Immune Subtype"),
    #             # c("Immune Subtype", "PCAWG Study"),
    #             ignore.order = T
    #         )
    #     })
    #
    #
    #     test_that("Get Valid Group Filters", {
    #         expect_equal(get_valid_group_filters(list()), list())
    #         expect_equal(get_valid_group_filters(list(NULL)), list())
    #         expect_equal(
    #             get_valid_group_filters(list("element1" = list("ids" = 1))),
    #             list()
    #         )
    #         expect_equal(
    #             get_valid_group_filters(
    #                 list("element1" = list("ids" = 1, "name" = "Group"))
    #             ),
    #             list(list("ids" = 1, "name" = "Group"))
    #         )
    #         expect_equal(
    #             get_valid_group_filters(
    #                 list(
    #                     "element1" = list("ids" = 1, "name" = "Group"),
    #                     "element2" = list("ids" = 1)
    #                 )
    #             ),
    #             list(list("ids" = 1, "name" = "Group"))
    #         )
    #         expect_equal(
    #             get_valid_group_filters(
    #                 list(
    #                     "element1" = list("ids" = 1, "name" = "Group"),
    #                     "element2" = list("ids" = 1, "name" = "Group")
    #                 )
    #             ),
    #             list(
    #                 list("ids" = 1, "name" = "Group"),
    #                 list("ids" = 1, "name" = "Group")
    #             )
    #         )
    #     })
    #
    #     test_that("Get Filtered Group Sample IDs", {
    #         expect_equal(get_filtered_group_sample_ids(list(), 1:10000), 1:10000)
    #         result1 <- get_filtered_group_sample_ids(
    #             list(
    #                 "element1" = list("ids" = 1, "name" = "Group"),
    #                 "element2" = list("ids" = 1, "name" = "Group")
    #             ),
    #             1:10000
    #         )
    #         expect_type(result1, "integer")
    #     })
    #
    #     test_that("Get Filtered Group Sample IDs By Filter", {
    #         expect_type(get_filtered_group_sample_ids_by_filter(1), "integer")
    #     })
    #
    #     test_that("Get Valid Numeric Filters", {
    #         expect_equal(get_valid_numeric_filters(list()), list())
    #         expect_equal(get_valid_numeric_filters(list(NULL)), list())
    #         expect_equal(
    #             get_valid_numeric_filters(list("element1" = list("id" = 1))),
    #             list()
    #         )
    #         expect_equal(
    #             get_valid_numeric_filters(
    #                 list("element1" = list("id" = 1, "min" = 1))
    #             ),
    #             list()
    #         )
    #         expect_equal(
    #             get_valid_numeric_filters(
    #                 list("element1" = list("id" = 1, "max" = 1))),
    #             list()
    #         )
    #         expect_equal(
    #             get_valid_numeric_filters(
    #                 list("element1" = list("id" = 1, "max" = 1, "min" = 0))
    #             ),
    #             list(list("id" = 1, "max" = 1, "min" = 0))
    #         )
    #         expect_equal(
    #             get_valid_numeric_filters(
    #                 list(
    #                     "element1" = list("id" = 1, "max" = 1, "min" = 0),
    #                     "element2" = list("id" = 1, "max" = 1)
    #                 )
    #             ),
    #             list(list("id" = 1, "max" = 1, "min" = 0))
    #         )
    #         expect_equal(
    #             get_valid_numeric_filters(
    #                 list(
    #                     "element1" = list("id" = 1, "max" = 1, "min" = 0),
    #                     "element2" = list("id" = 1, "max" = 1, "min" = 0)
    #                 )
    #             ),
    #             list(
    #                 list("id" = 1, "max" = 1, "min" = 0),
    #                 list("id" = 1, "max" = 1, "min" = 0)
    #             )
    #         )
    #     })
    #
    #     test_that("Is Numeric Filter Valid", {
    #         expect_false(is_numeric_filter_valid(NULL))
    #         expect_false(is_numeric_filter_valid(list("id" = 1, "max" = 1)))
    #         expect_false(
    #             is_numeric_filter_valid(list("id" = 1, "min" = 0, "mx" = 1))
    #         )
    #     })
    #
    #     test_that("Get Filtered Feature Sample IDs", {
    #         expect_equal(get_filtered_feature_sample_ids(list(), 1:10000), 1:10000)
    #         result1 <- get_filtered_feature_sample_ids(
    #             list(
    #                 "element1" = list("id" = 1L, "max" = 10000, "min" = -10000),
    #                 "element2" = list("id" = 1L, "max" = 10000, "min" = -10000)
    #             ),
    #             1:10000
    #         )
    #         expect_type(result1, "integer")
    #     })
    #
    #     test_that("Create Cohort Filter Object", {
    #         expect_equal(
    #             create_cohort_filter_object(1:1000, list(), list()),
    #             list(
    #                 "sample_ids" = 1:1000,
    #                 "filters" = list(
    #                     "feature_filters" = list(),
    #                     "group_filters" = list()
    #                 )
    #             )
    #         )
    #     })
#
#     test_that("Get Filtered Feature Sample IDs", {
#         expect_equal(get_filtered_feature_sample_ids(list(), 1:10000), 1:10000)
#         result1 <- get_filtered_feature_sample_ids(
#             list(
#                 "element1" = list("id" = 1L, "max" = 10000, "min" = -10000),
#                 "element2" = list("id" = 1L, "max" = 10000, "min" = -10000)
#             ),
#             1:10000
#         )
#         expect_type(result1, "integer")
#     })
#
#     test_that("Create Cohort Filter Object", {
#         expect_equal(
#             create_cohort_filter_object(1:1000, list(), list()),
#             list(
#                 "sample_ids" = 1:1000,
#                 "filters" = list(
#                     "feature_filters" = list(),
#                     "group_filters" = list()
#                 )
#             )
#         )
#     })
})
