
test_that("Create Cohort Module String", {
    tbl1 <- dplyr::tibble(
        dataset = c(rep("DS1", 5), rep("DS2", 3)),
        module = c(paste0("M", 1:5), paste0("M", 1:3))
    )
    expect_equal(
        create_cohort_module_string("DS1", tbl1),
        "Modules available for dataset DS1: M1, M2, M3, M4, M5"
    )
    expect_equal(
        create_cohort_module_string("DS2", tbl1),
        "Modules available for dataset DS2: M1, M2, M3"
    )
    expect_equal(
        create_cohort_module_string("DS1"),
        "No modules currently available for selected dataset"
    )
})

