test_that("til_map_datatable_server", {
  shiny::testServer(
    til_map_datatable_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      )
    ),
    {

      expect_type(patient_tbl(), "list")
      expect_named(patient_tbl(), c("sample_name",  "patient_name"))
      expect_true(nrow(patient_tbl()) > 0)

      expect_type(slide_tbl(), "list")
      expect_named(slide_tbl(), c("patient_name", "slide_name"))
      expect_true(nrow(slide_tbl()) > 0)

      expect_type(feature_tbl(), "list")
      expect_true(nrow(feature_tbl()) > 0)

      expect_type(tilmap_tbl(), "list")
      expect_true(nrow(tilmap_tbl()) > 0)
    }
  )
})
