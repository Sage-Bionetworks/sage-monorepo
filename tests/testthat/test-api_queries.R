with_test_api_env({

  test_that("query_cohort_selector", {
    result <- query_cohort_selector()
    expect_named(
      result,
      c(
        "characteristics",
        "color",
        "display",
        "name",
        "sampleCount",
        "samples"
      )
    )
  })

  test_that("query_features_by_class", {
    result <- query_features_by_class()
    expect_named(
      result,
      c(
        "class",
        "features"
      )
    )
  })

  test_that("query_samples_to_features", {
    result <- query_samples_to_features("leukocyte_fraction")
    expect_named(
      result,
      c(
        "name",
        "sample",
        "value"
      )
    )
  })

  test_that("query_samples_to_feature", {
    result <- query_samples_to_feature("leukocyte_fraction")
    expect_named(
      result,
      c(
        "sample",
        "value"
      )
    )
  })
})


