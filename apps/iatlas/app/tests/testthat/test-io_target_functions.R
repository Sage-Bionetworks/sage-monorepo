
test_that("Get Gene From URL", {
    expect_equal(get_gene_from_url(list()), NA)
    expect_equal(get_gene_from_url(list("gene" = "gene1")), "gene1")
})

io_target_tbl <- iatlas.api.client::query_io_targets()

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

