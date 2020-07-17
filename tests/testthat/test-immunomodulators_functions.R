with_test_api_env({

    test_that("Create Immunomodulators Gene List", {
        tbl1 <- dplyr::tibble(
            hgnc   = c("g1", "g2", "g3"),
            entrez = 1:3,
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

    test_that("Build Immunomodulators Datatable Tibble", {
        res1 <- build_im_dt_tbl()
        expect_named(
            res1,
            c("Hugo", "Entrez ID", "Friendly Name", "Gene Family",
              "Super Category", "Immune Checkpoint", "Function",
              "Reference(s) [PMID]"
            )
        )

        # ARG1_publications <- res1 %>%
        #     dplyr::filter(.data$`Entrez ID` == 383L) %>%
        #     dplyr::pull("Reference(s) [PMID]")
        #
        # expect_equal(
        #     ARG1_publications,
        #     c(
        #         "https://www.ncbi.nlm.nih.gov/pubmed/19764983",
        #         "https://www.ncbi.nlm.nih.gov/pubmed/23890059"
        #     )
        # )
    })

})
