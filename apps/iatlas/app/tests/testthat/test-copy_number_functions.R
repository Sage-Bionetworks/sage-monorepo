
group_tbl <- iatlas.api.client::query_tags(
  cohorts = "TCGA",
  parent_tags = "Immune_Subtype"
) %>%
  dplyr::select("display" = "tag_short_display", "name" = "tag_name")

gene_tbl <- iatlas.api.client::query_genes(entrez = 1:300) %>%
  dplyr::select("entrez", "hgnc")

im_gene_tbl <-
  iatlas.api.client::query_genes(gene_types = "immunomodulator") %>%
  dplyr::select("entrez", "hgnc")

gene_set_tbl <- iatlas.api.client::query_gene_types()

result_tbl<- iatlas.api.client::query_copy_number_results(
  datasets = "TCGA",
  tags = c("C1", "C2"),
  entrez = 1:2,
  features = "AS"
)

test_that("build_cnv_group_list", {
  result <- build_cnv_group_list(group_tbl)
  items <- c("All", "C1", "C2", "C3", "C4", "C5", "C6")
  expect_equal(
    result,
    purrr::set_names(items, items)
  )
})

test_that("build_cnv_gene_list", {
  result <- build_cnv_gene_list(gene_set_tbl, gene_tbl)
  expect_named(result, c("All Genes", "Gene Sets", "Genes"))
})

test_that("get_cnv_entrez_query_from_filters", {

  result1 <- get_cnv_entrez_query_from_filters("All", gene_set_tbl, gene_tbl)
  expect_equal(result1, gene_tbl$entrez)

  result2 <- get_cnv_entrez_query_from_filters(
    c("All", "immunomodulators"), gene_set_tbl, gene_tbl
  )

  expect_equal(result2, gene_tbl$entrez)

  result3 <- get_cnv_entrez_query_from_filters(
    "immunomodulator", gene_set_tbl, gene_tbl
  )
  expect_type(result3, "integer")
  expect_length(result3, 78)

  result4 <- get_cnv_entrez_query_from_filters(
    c("10148", "6346",  "4940",  "27074", "29880"), gene_set_tbl, gene_tbl
  )
  expect_equal(result4,  c(10148, 6346, 4940, 27074, 29880))
})

test_that("create_cnv_results_string", {
  result <- create_cnv_results_string(result_tbl)
  expect_equal(result, "Total number of rows: 8, Number of genes: 2")
})

test_that("build_cnv_dt_tbl", {
  result <- build_cnv_dt_tbl(result_tbl)
  expect_named(
    result,
    c(
      "Metric",
      "Group",
      "Gene",
      "Direction",
      "Mean Normal",
      "Mean CNV",
      "Mean Diff",
      "T stat",
      "Neg log10 pvalue"
    )
  )
})

