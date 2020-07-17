with_test_api_env({

    test_that("Get Gene From URL", {
        expect_equal(get_gene_from_url(list()), NA)
        expect_equal(get_gene_from_url(list("gene" = "gene1")), "gene1")
    })

    io_target_tbl <- iatlas.app::query_io_targets()

    test_that("Create IO Target Gene List", {
        result1 <- create_io_target_gene_list(io_target_tbl, "pathway")
        expect_named(result1, "g1")
        result2 <- create_io_target_gene_list(tbl1, "therapy_type")
        expect_named(result2, c("g2", "g3"))
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
    # test_that("Create IO Landscape Links", {
    #     tbl1 <- dplyr::tibble(
    #         hgnc          = c("g1", "g2"),
    #         entrez        = 1:2,
    #         friendly_name = c("g3", "g4"),
    #         pathway       = c("p1", "p2"),
    #         therapy       = c("t1", "t2"),
    #         description   = c("d1", "d2")
    #     )
    #     result1 <- build_io_target_dt_tbl(tbl1)
    #     expect_named(
    #         result1,
    #         c("Hugo", "Entrez ID", "Friendly Name", "Pathway", "Therapy Type",
    #           "Description", "Link to IO Landscape"
    #         )
    #     )
    # })
    #
    # test_that("Create IO Landscape Links", {
    #     expect_equal(
    #         create_io_landscape_links("CD19"),
    #         paste0(
    #             "<a href=\'",
    #             "https://www.cancerresearch.org/scientists/",
    #             "immuno-oncology-landscape?viz1572545060618=2017;Target;CD19",
    #             "\'>CD19</a>"
    #         )
    #     )
    # })
})
