test_that("Create Class List", {
    result1 <- create_data_info_class_list()
    expect_type(result1, "integer")
    expect_true("All classes" %in% names(result1))
    expect_equal(result1[["All classes"]], -1L)
})

test_that("Create Feature Tibble", {
    result1 <- create_feature_tbl(1L)
    expect_named(
        result1,
        c("feature", "unit", "class_id", "order", "class", "method_tag")
    )
    expect_true(1L %in%  result1$class_id)
})

test_that("Format Feature Tibble", {
    result1 <- format_feature_tbl(dplyr::tibble(
        feature = "", unit = "", class = ""
    ))
    expect_named(result1, c("Feature Name", "Variable Class", "Unit"))
})

test_that("Filter Feature Tibble", {
    tbl <- dplyr::tibble(class_id = c(1L, 1L, 2L))
    result1 <- filter_feature_tbl(tbl, 1L)
    result2 <- filter_feature_tbl(tbl, 2L)
    result3 <- filter_feature_tbl(tbl, 3L)
    expect_named(result1, "class_id")
    expect_named(result2, "class_id")
    expect_named(result3, "class_id")
    expect_equal(result1$class_id, c(1L, 1L))
    expect_equal(result2$class_id, c(1L, 1L))
    expect_equal(result3$class_id, 2L)
})

test_that("Format Filtered Feature Tibble", {
    result1 <- format_filtered_feature_tbl(dplyr::tibble(
        feature = "", unit = "", method_tag = "", order = 1
    ))
    expect_named(
        result1,
        c("Variable Class Order", "Feature Name", "Unit", "Methods Tag")
    )
})

test_that("Get Selected Method Tags", {
    result1 <- format_filtered_feature_tbl(dplyr::tibble(
        feature = "", unit = "", method_tag = "", order = 1
    ))
    expect_named(
        result1,
        c("Variable Class Order", "Feature Name", "Unit", "Methods Tag")
    )
})

