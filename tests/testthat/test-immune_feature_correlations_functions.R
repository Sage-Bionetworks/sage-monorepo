test_that("Build Immune Feature Correlations Value Table", {
    response_tbl <- dplyr::tibble(
        sample = c("A", "B", "C"),
        name   = "F1",
        value  = 1:3
    )
    feature_tbl  <- dplyr::tribble(
        ~sample,    ~feature_name,    ~value, ~order, ~feature2,
        "A",        "F1",        .1,     2,      "f1",
        "A",        "F2",        .2,     1,      "f2",
        "A",        "F3",        .3,     3,      "f3",
        "B",        "F1",        .4,     2,      "f1",
        "B",        "F2",        .5,     1,      "f2",
        "B",        "F3",        .6,     3,      "f3"
    )
    sample_tbl <- dplyr::tibble(sample = c("A", "B", "C"), group = rep("C1", 3))
    result1 <- build_ifc_value_tbl(response_tbl, feature_tbl, sample_tbl)
    expect_named(
        result1,
        c("response_value",
          "feature_value",
          "feature_name",
          "sample",
          "order",
          "group"
        )
    )
})

test_that("Build Immune Feature Correlations Heatmap Matrix", {
    tbl <- dplyr::tribble(
        ~order, ~feature_value, ~response_value, ~feature_name, ~group,
        1,      3,              .1,              "f1",          "C1",
        2,      2,              .2,              "f2",          "C1",
        NA,     1,              .7,              "f3",          "C1",
        1,      4,              .4,              "f1",          "C1",
        2,      2,              .5,              "f2",          "C1",
        NA,     3,              .2,              "f3",          "C1",
        1,      2,              .1,              "f1",          "C1",
        2,      2,              .2,              "f2",          "C1",
        NA,     7,              .9,              "f3",          "C1",
        1,      1,              .4,              "f1",          "C1",
        2,      8,              .5,              "f2",          "C1",
        NA,     2,              .5,              "f3",          "C1",
    )
    result1 <- build_ifc_heatmap_matrix(tbl, "pearson")
    expect_equal(rownames(result1), c("f2", "f1", "f3"))
    expect_equal(colnames(result1), "C1")
})

test_that("Build Immune Feature Scatterplot Tibble", {
    tbl <- dplyr::tribble(
        ~sample_id, ~feature_value, ~response_value, ~feature_name, ~group,
        1,          1,              .1,              "f1",          "C1",
        1,          2,              .2,              "f2",          "C1",
        2,          1,              .4,              "f1",          "C2",
        2,          2,              .5,              "f2",          "C2",
        3,          1,              .1,              "f1",          "C1",
        3,          2,              .2,              "f2",          "C1",
        4,          1,              .4,              "f1",          "C2",
        4,          2,              .5,              "f2",          "C2",
    )
    sample_tbl <- dplyr::tibble(sample_id = 1:2, sample_name = c("N1", "N2"))
    result1 <- build_ifc_scatterplot_tbl(tbl, sample_tbl, "f1", "C2")
    expect_named(result1, c("group", "name", "label", "x", "y"))
})
