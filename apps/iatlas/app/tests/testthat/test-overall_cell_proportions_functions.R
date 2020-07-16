with_test_api_env({

    test_that("Build Overall Cell Proportions Value Tibble", {
        tbl1 <- dplyr::tibble(
            sample_id = c(1:10),
            group = c(rep("G1", 5), rep("G2", 5))
        )
        result1 <- build_ocp_value_tbl(tbl1)
        expect_named(
            result1,
            c("feature_name", "order", "sample_id", "feature_value", "group")
        )
    })

    test_that("Build Overall Cell Proportions Barplot Tibble", {
        tbl1 <- dplyr::tibble(
            feature_name = c(rep("f1", 5), rep("f2", 5)),
            feature_value = 1:10,
            group =  unlist(
                purrr::map2(rep("G1", 5), rep("G2", 5), ~c(.x, .y))
            ),
            order = c(rep(1, 5), rep(2, 5))
        )
        result1 <- build_ocp_barplot_tbl(tbl1)
        expect_named(
            result1,
            c("x", "y", "color", "label", "error")
        )
    })

    test_that("Build Overall Cell Proportions Scatterplot Tibble", {
        value_tbl <- dplyr::tibble(
            feature_name = c(rep("Leukocyte Fraction", 5), rep("Stromal Fraction", 5)),
            feature_value = 0.5 + rnorm(10, sd = .1),
            group =  "G1",
            order = 1,
            sample_id = c(1:5, 1:5)
        )
        sample_tbl <- dplyr::tibble(
            sample_id = 1:10,
            sample_name = paste0("S", 1:10)
        )

        result1 <- build_ocp_scatterplot_tbl(value_tbl, sample_tbl, "G1")
        expect_named(
            result1,
            c("x", "y", "label")
        )
    })
})
