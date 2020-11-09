
cohort_obj1 <- pcawg_immune_subtype_cohort_obj
cohort_obj2 <- pcawg_feature_bin_cohort_obj

test_that("Get Gene From URL", {
    expect_equal(get_gene_from_url(list()), NA)
    expect_equal(get_gene_from_url(list("gene" = "gene1")), "gene1")
})

io_target_tbl <- iatlas.api.client::query_io_targets()

test_that("Create IO Target Gene List", {
    result <- create_io_target_gene_list(io_target_tbl, "therapy_type")
    expect_named(
        result,
        c(
            'Targeted by Other Immuno-Oncology Therapy Type',
            'Other immunomodulator',
            'T-cell targeted immunomodulator'
        )
    )
})

test_that("get_io_hgnc_from_tbl", {
    res1 <- get_io_hgnc_from_tbl(io_target_tbl, 135L)
    expect_equal(res1, "ADORA2A")
})

test_that("Build IO Target Distplot Tibble", {
    expected_columns <- c("x", "y", "label")
    res1 <- build_io_target_distplot_tbl(cohort_obj1, 3802L, "None")
    res2 <- build_io_target_distplot_tbl(cohort_obj2, 3802L, "None")
    expect_named(res1, expected_columns)
    expect_named(res2, expected_columns)
})

test_that("Build IO Target Datatable Tibble", {
    result <- build_io_target_dt_tbl(io_target_tbl)
    expect_named(
        result,
        c(
            "Hugo",
            "Entrez ID",
            "Friendly Name",
            "Pathway",
            "Therapy Type",
            "Description",
            "Link to IO Landscape"
        )
    )
})

test_that("Create IO Landscape Links", {
    expect_equal(
        create_io_landscape_links("CD19"),
        stringr::str_c(
            "<a href='",
            "https://www.cancerresearch.org/scientists/",
            "immuno-oncology-landscape?viz1572545060618=2017;Target;CD19",
            "'>CD19</a>"
        )
    )
})

