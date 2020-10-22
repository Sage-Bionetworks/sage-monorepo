
cohort_obj1 <- pcawg_immune_subtype_cohort_obj
cohort_obj2 <- pcawg_feature_bin_cohort_obj


im_tbl <- iatlas.api.client::query_immunomodulators()

test_that("Create Immunomodulators Gene List", {
    res1 <- create_im_gene_list(im_tbl, "gene_family")
    res2 <- create_im_gene_list(im_tbl, "super_category")
    res3 <- create_im_gene_list(im_tbl, "immune_checkpoint")
    expect_named(res1, sort(unique(im_tbl$gene_family)))
    expect_named(res2, sort(unique(im_tbl$super_category)))
    expect_named(res3, sort(unique(im_tbl$immune_checkpoint)))
})

test_that("get_im_hgnc_from_tbl", {
    res1 <- get_im_hgnc_from_tbl(im_tbl, 135L)
    expect_equal(res1, "ADORA2A")
})

test_that("Build Immunomodulators Distributions Plot Tibble", {
    expected_columns <- c("x", "y")
    res1 <- build_im_distplot_tbl(cohort_obj1, 3802L, "None")
    res2 <- build_im_distplot_tbl(cohort_obj1, 3802L, "None")
    expect_named(res1, expected_columns)
    expect_named(res2, expected_columns)
})

# TODO: add back in ref
test_that("Build Immunomodulators Datatable Tibble", {
    res1 <- build_im_dt_tbl()
    expect_named(
        res1,
        c("Hugo", "Entrez ID", "Friendly Name", "Gene Family",
          "Super Category", "Immune Checkpoint", "Function"
          # "Reference(s) [PMID]"
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

