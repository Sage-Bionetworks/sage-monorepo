test_that("is_tag_filter_valid", {
    expect_false(is_tag_filter_valid(NULL))
    expect_false(is_tag_filter_valid(list("tags" = NULL)))
    expect_true(is_tag_filter_valid(list("tags" = "C1")))
    expect_true(is_tag_filter_valid(list("tags" = list("C1", "C2"))))
})

test_that("get_valid_tag_filters", {
    expect_equal(get_valid_tag_filters(list()), list())
    expect_equal(get_valid_tag_filters(list(NULL)), list())
    expect_equal(
        get_valid_tag_filters(list("element1" = list("tags" = "C1"))),
        list(list("tags" = "C1"))
    )
    expect_equal(
        get_valid_tag_filters(
            list(
                "element1" = list("tags" = "C1"),
                "element2" = list(NULL)
            )
        ),
        list(list("tags" = "C1"))
    )
    expect_equal(
        get_valid_tag_filters(
            list(
                "element1" = list("tags" = "C1"),
                "element2" = list("tags" = "BRCA")
            )
        ),
        list(list("tags" = "C1"), list("tags" = "BRCA"))
    )
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
