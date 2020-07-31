with_test_api_env({

  cohort_obj1 <- build_cohort_object(
    filter_obj = list(
      "samples" = "PCAWG" %>%
        query_dataset_samples(.) %>%
        dplyr::pull("name")
    ),
    dataset = "PCAWG",
    group_choice = "Immune_Subtype",
    group_type = "tag"
  )

  test_that("Build Distribution Plot Tibble", {

    result1 <- build_ifd_distplot_tbl(cohort_obj1, "leukocyte_fraction", "None")
    expect_named(result1, c("x", "y"))
  })
})
