with_test_db_env({
    test_that("Get Survival Status ID from Time ID", {
        time_id1 <- get_feature_id_from_display("OS Time")
        expect_equal(typeof(time_id1), "integer")
        status_id1 <- get_status_id_from_time_id(time_id1)
        expect_equal(typeof(status_id1), "integer")
        status_name1 <- get_feature_display_from_id(status_id1)
        expect_equal(status_name1, "OS")

        time_id2 <- get_feature_id_from_display("PFI Time")
        expect_equal(typeof(time_id2), "integer")
        status_id2 <- get_status_id_from_time_id(time_id2)
        expect_equal(typeof(status_id2), "integer")
        status_name2 <- get_feature_display_from_id(status_id2)
        expect_equal(status_name2, "PFI")
    })

    test_that("Build Survival Values Tibble", {
        sample_tbl <- dplyr::tibble(
            sample_id = 1:3,
            group = c("C1", "C2", "C3")
        )
        result1 <- build_survival_value_tbl(sample_tbl, 1, 2)
        expect_named(result1, c("group", "time", "status", "sample_id"))
    })

    test_that("Build Heatmap Matrix", {
        tbl <- dplyr::tibble(
            feature = "feature",
            value = rnorm(40, 10, 3),
            time = round(rnorm(40, 10, 3)),
            status = rbinom(40, 1, .5),
            group = c(rep("C1", 20), rep("C2", 20))
        )
        result <- build_heatmap_matrix(tbl)
        expect_type(result, "double")
        expect_equal(rownames(result), "feature")
        expect_equal(colnames(result), c("C1", "C2"))
    })
})
