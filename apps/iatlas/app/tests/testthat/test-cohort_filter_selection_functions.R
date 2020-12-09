test_that("is_group_filter_valid", {
  expect_false(is_group_filter_valid(NULL))
  expect_false(is_group_filter_valid(list("group_choices" = NULL)))
  expect_false(is_group_filter_valid(list("group_choices" = "C1")))
  expect_false(
    is_group_filter_valid(list(
      "group_choices" = "C1",
      "group_type" = "tag",
      "parent_group_choice" = NULL
    ))
  )
  expect_true(
    is_group_filter_valid(list(
      "group_choices" = "C1",
      "group_type" = "tag",
      "parent_group_choice" = "Immnune_Subtype"
    ))
  )
  expect_true(
    is_group_filter_valid(list(
      "group_choices" = list("C1", "C2"),
      "group_type" = "tag",
      "parent_group_choice" = "Immnune_Subtype"
    ))
  )
})

test_that("get_valid_group_filters", {
  invalid1 <- list()
  invalid2 <- list(NULL)
  valid1 <- list(
    "group_choices" = "C1",
    "group_type" = "tag",
    "parent_group_choice" = "Immnune_Subtype"
  )
  valid2 <- list(
    "group_choices" = "BRCA",
    "group_type" = "tag",
    "parent_group_choice" = "TCGA_Study"
  )
  expect_equal(get_valid_group_filters(invalid1), list())
  expect_equal(get_valid_group_filters(invalid2), list())
  expect_equal(
    get_valid_group_filters(list(
      "element1" = valid1
    )),
    list(valid1)
  )
  expect_equal(
    get_valid_group_filters(list(
        "element1" = valid1,
        "element2" = invalid1
    )),
    list(valid1)
  )
  expect_equal(
    get_valid_group_filters(
      list(
        "element1" = valid1,
        "element2" = valid2
      )
    ),
    list(valid1, valid2)
  )
})

test_that("get_group_filtered_samples", {
  filter_obj <- list(
    list(
      "group_choices" = c("C5", "C6"),
      "group_type" = "tag",
      "parent_group_choice" = "Immnune_Subtype"
    ),
    list(
      "group_choices" = c("COAD", "STAD"),
      "group_type" = "tag",
      "parent_group_choice" = "TCGA_Study"
    ),
    list(
      "group_choices" = c("male", "female"),
      "group_type" = "clinical",
      "parent_group_choice" = "gender"
    ),
    list(
      "group_choices" = c("not hispanic or latino"),
      "group_type" = "clinical",
      "parent_group_choice" = "ethnicity"
    )
  )

  samples <- iatlas.api.client::query_dataset_samples("TCGA")$name
  result1 <- get_group_filtered_samples(filter_obj, samples, "TCGA")
  expect_type(result1, "character")
  expect_true(length(result1) > 0)
})

test_that("get_filtered_tag_samples", {
    filter_obj <- list(
        list("tags" = c("C1", "C2", "C3", "C4", "C6")),
        list("tags" = c("CLLE-ES", "MALY-DE"))
    )
    pcawg_samples <- iatlas.api.client::query_dataset_samples("PCAWG")$name
    result1 <- get_filtered_tag_samples(filter_obj, pcawg_samples, "PCAWG")
    expect_type(result1, "character")
    expect_true(length(result1) > 0)
})

test_that("Is Numeric Filter Valid", {
    expect_false(is_numeric_filter_valid(NULL))
    expect_false(is_numeric_filter_valid(list("feature" = "a", "max" = 1)))
    expect_false(
        is_numeric_filter_valid(list("feature" = "a", "min" = 0, "mx" = 1))
    )
    expect_true(
        is_numeric_filter_valid(list("feature" = "a", "min" = 0, "max" = 1))
    )
})

test_that("get_valid_numeric_filters", {
    expect_equal(get_valid_numeric_filters(list()), list())
    expect_equal(get_valid_numeric_filters(list(NULL)), list())
    expect_equal(
        get_valid_numeric_filters(list("element1" = list("feature" = "a"))),
        list()
    )
    expect_equal(
        get_valid_numeric_filters(
            list("element1" = list("feature" = "a", "min" = 1))
        ),
        list()
    )
    expect_equal(
        get_valid_numeric_filters(
            list("element1" = list("feature" = "a", "max" = 1))),
        list()
    )
    expect_equal(
        get_valid_numeric_filters(
            list("element1" = list("feature" = "a", "max" = 1, "min" = 0))
        ),
        list(list("feature" = "a", "max" = 1, "min" = 0))
    )
    expect_equal(
        get_valid_numeric_filters(
            list(
                "element1" = list("feature" = "a", "max" = 1, "min" = 0),
                "element2" = list("feature" = "b", "max" = 1)
            )
        ),
        list(list("feature" = "a", "max" = 1, "min" = 0))
    )
    expect_equal(
        get_valid_numeric_filters(
            list(
                "element1" = list("feature" = "a", "max" = 1, "min" = 0),
                "element2" = list("feature" = "b", "max" = 1, "min" = 0)
            )
        ),
        list(
            list("feature" = "a", "max" = 1, "min" = 0),
            list("feature" = "b", "max" = 1, "min" = 0)
        )
    )
})

test_that("get_filtered_feature_samples", {
    filter_obj <- list(
        "element1" = list("feature" = "B_cells_memory", "max" = 1, "min" = 0),
        "element2" = list("feature" = "B_cells_naive", "max" = 1, "min" = 0)
    )
    pcawg_samples <- iatlas.api.client::query_dataset_samples("PCAWG")$name
    result1 <- get_filtered_feature_samples(filter_obj, pcawg_samples, "PCAWG")
    expect_type(result1, "character")
    expect_true(length(result1) > 0)
})
