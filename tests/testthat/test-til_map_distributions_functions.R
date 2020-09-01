with_test_api_env({

    cohort_obj1 <- build_cohort_object(
        filter_obj = list(
            "samples" = "TCGA" %>%
                query_dataset_samples(.) %>%
                dplyr::pull("name")
        ),
        dataset = "TCGA",
        group_choice = "Immune_Subtype",
        group_type = "tag"
    )

    test_that("Create Tilmap Named List", {
        result1 <- create_tm_named_list(cohort_obj1)
        expect_named(result1)
    })

    test_that("Build Tilmap Distplot Tibble", {
        result1 <- build_tm_distplot_tbl(cohort_obj1, "til_percentage", "None")
        expect_named(result1, c("x", "y"))
    })
})
