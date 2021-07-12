test_that("til_map_datatable_server", {
  shiny::testServer(
    til_map_datatable_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(get_tcga_immune_subtype_cohort_obj_50())
    ),
    {
      expect_type(tilmap_tbl(), "list")
    }
  )
})
