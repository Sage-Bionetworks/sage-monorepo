
test_that("Build Immunomodulators Datatable Tibble", {
    res1 <- build_im_dt_tbl()
    expect_named(
        res1,
        c("Hugo", "Entrez ID", "Friendly Name", "Gene Family",
          "Super Category", "Immune Checkpoint", "Function",
          'Reference(s) [PMID]'
        )
    )
})

