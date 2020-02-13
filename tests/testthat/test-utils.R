

test_that("Create Plotly Label", {
    tbl <- dplyr::tribble(
        ~name,   ~name2,  ~group, ~v1, ~v2,
        "name1", "name5", "g1",   1,   1.1,
        "name2", "name6", "g1",   2,   2.2,
        "name3", "name7", "g2",   3,   3.3,
        "name4", "name8", "g2",   4,   4.4,
    )
    result1 <- create_plotly_label(
        tbl, name, group, c("v1", "v2"), title = "title"
    )
    result2 <- create_plotly_label(
        tbl, name2, group, c("v1", "v2"), title = "title"
    )
    expect_named(result1, c("name", "name2", "group", "label", "v1", "v2"))
    expect_named(result2, c("name", "name2", "group", "label", "v1", "v2"))
    expect_equal(
        result1$label[[1]],
        "<b>title:</b> name1 (g1)</br></br>V1: 1.000</br>V2: 1.100"
    )
    expect_equal(
        result1$label[[3]],
        "<b>title:</b> name3 (g2)</br></br>V1: 3.000</br>V2: 3.300"
    )
    expect_equal(
        result2$label[[1]],
        "<b>title:</b> name5 (g1)</br></br>V1: 1.000</br>V2: 1.100"
    )
    expect_equal(
        result2$label[[3]],
        "<b>title:</b> name7 (g2)</br></br>V1: 3.000</br>V2: 3.300"
    )
})

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

