with_test_api_env({

    test_that("Get Gene From URL", {
        expect_equal(get_gene_from_url(list()), NA)
        expect_equal(get_gene_from_url(list("gene" = "gene1")), "gene1")
    })

    io_target_tbl <- query_io_targets()

    test_that("Create IO Target Gene List", {
        result <- create_io_target_gene_list(io_target_tbl, "therapy_type")
        expect_named(
            result,
            c(
                'Targeted by Other Immuno-Oncology Therapy Type',
                'T-cell targeted immunomodulator', 'Other immunomodulator'
            )
        )
    })
    #
    # test_that("Build IO Target Distplot Tibble", {
    #     tbl1 <- dplyr::tibble(
    #         sample_id = 1:10000,
    #         group = "G1"
    #     )
    #     result1 <- build_io_target_distplot_tbl(1L, tbl1, "None")
    #     expect_named(
    #         result1,
    #         c("x", "y")
    #     )
    # })
    #
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
})
