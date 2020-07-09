with_test_db_env({

    test_that("Create Immunomodulators Gene List", {
        tbl1 <- dplyr::tibble(
            hgnc   = c("g1", "g2", "g3"),
            id     = 1:3,
            group1 = c("A", "B", "B"),
            group2 = c("C", "C", "D")
        )
        res1 <- create_im_gene_list(tbl1, "group1")
        res2 <- create_im_gene_list(tbl1, "group2")
        expect_named(res1, c("A", "B"))
        expect_named(res1$A, "g1")
        expect_named(res1$B, c("g2", "g3"))
        expect_named(res2, c("C", "D"))
    })

    test_that("Create Immunomodulators Gene List", {
        tbl1 <- dplyr::tibble(
            sample_id = c(26, 31, 90, 91, 3631),
            group = c("G1", "G1", "G2", "G2", "G2")
        )
        res1 <- build_im_distplot_tbl(17, tbl1, "None")
        expect_named(res1, c("x", "y"))
    })

    test_that("Build Immunomodulators Distributions Plot Tibble", {
        tbl1 <- dplyr::tibble(
            sample_id = c(26, 31, 90, 91, 3631),
            group = c("G1", "G1", "G2", "G2", "G2")
        )
        res1 <- build_im_distplot_tbl(17, tbl1, "None")
        expect_named(res1, c("x", "y"))
    })

    test_that("Build Immunomodulators Datatable Tibble", {
        tbl1 <- dplyr::tibble(
            hgnc = "g",
            entrez = 1,
            friendly_name = "name",
            gene_family = "family",
            super_category = "cat",
            immune_checkpoint = "ic",
            gene_function = "func",
            references = "[{r1, r2}]"
        )
        res1 <- build_im_dt_tbl(tbl1)
        expect_named(
            res1,
            c("Hugo", "Entrez ID", "Friendly Name", "Gene Family",
              "Super Category", "Immune Checkpoint", "Function",
              "Reference(s) [PMID]"
            )
        )
    })

})
