test_that("Create Tilmap Named List", {
    result1 <- create_tm_named_list(tcga_immune_subtype_cohort_obj_50)
    expect_named(result1)
})

test_that("Build Tilmap Distplot Tibble", {
    result1 <- build_tm_distplot_tbl(
      tcga_immune_subtype_cohort_obj_50, "til_percentage", "None"
    )
    expect_named(result1, c("x", "y"))
})

