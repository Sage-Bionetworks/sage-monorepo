
cohort_obj1 <- tcga_immune_subtype_cohort_obj_50

test_that("Create Tilmap Named List", {
    result1 <- create_tm_named_list(cohort_obj1)
    expect_named(result1)
})

test_that("Build Tilmap Distplot Tibble", {
    result1 <- build_tm_distplot_tbl(cohort_obj1, "til_percentage", "None")
    expect_named(result1, c("x", "y"))
})

