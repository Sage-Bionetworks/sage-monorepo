test_that("Create Feature Named List", {
    result1 <- create_feature_named_list()
    expect_equal(typeof(result1), "list")
    expect_equal(typeof(unlist(result1)), "integer")
})

test_that("Create Nested Named List", {
    tbl    <- dplyr::tibble(
        class   = c(rep("Class1", 3), "Class2"),
        display = c("feature1", "feature2", "feature3", "feature4"),
        feature = 1:4
    )
    result1 <- create_nested_named_list(tbl)
    expect_named(result1, c("Class1", "Class2"))
    expect_length(result1, 2)
    expect_named(result1$Class1, c("feature1", "feature2", "feature3"))
    expect_length(result1$Class1, 3)
    expect_named(result1$Class2, "feature4")
    expect_length(result1$Class2, 1)
})

test_that("Scale Tibble Value Column", {
    tbl <- dplyr::tibble(value = 1:5)
    result1 <- scale_tbl_value_column(tbl)
    expect_named(result1, "value")
    result2 <- scale_tbl_value_column(tbl, scale_method = "Log2")
    expect_named(result2, "value")
    result3 <- scale_tbl_value_column(tbl, scale_method = "Log2 + 1")
    expect_named(result3, "value")
    expect_error(
        scale_tbl_value_column(tbl, scale_method = "Not a method"),
        regexp = "Scale method does not exist",
        fixed = T
    )
})

test_that("Log Tibble Value Column", {
    tbl <- dplyr::tibble(value = 1:5)
    result <- log_tbl_value_column(tbl)
    expect_named(result, "value")
})

test_that("Get Values from Eventdata Dataframe", {
    df <- data.frame(x = c(rep("C1", 3)), y = 1:3, stringsAsFactors = F)
    expect_equal(get_values_from_eventdata(df), "C1")
})

