with_test_db_env({
    test_that("Build Immunomodulators Table", {
        result1 <- build_im_tbl()
        expect_named(
            result1,
            c("id", "hgnc", "entrez", "friendly_name", "references",
              "gene_family","super_category", "immune_checkpoint",
              "gene_function"
            )
        )
    })

    test_that("Create Build Immunomodulators Table Query", {
        expect_equal(
            create_build_im_tbl_query(),
            paste0(
                "SELECT a.id, a.hgnc, a.entrez, a.friendly_name, a.references, ",
                "gf.name AS gene_family, sc.name as super_category, ic.name AS ",
                "immune_checkpoint, gfunc.name as gene_function FROM (",
                "SELECT * FROM genes WHERE id IN ",
                "(SELECT gene_id FROM genes_to_types WHERE type_id IN ",
                "(SELECT id FROM gene_types WHERE name IN ('immunomodulator')))) a " ,
                "LEFT JOIN gene_families gf ON a.gene_family_id = gf.id ",
                "LEFT JOIN super_categories sc ON a.super_cat_id = sc.id ",
                "LEFT JOIN immune_checkpoints ic ON a.immune_checkpoint_id = ic.id ",
                "LEFT JOIN gene_functions gfunc ON a.gene_function_id = gfunc.id"
            )
        )
    })

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
