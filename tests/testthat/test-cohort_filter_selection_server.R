
test_that("cohort_filter_selection_server_pcawg", {
  shiny::testServer(
    cohort_filter_selection_server,
    args = list("dataset" = shiny::reactiveVal("PCAWG")),
    {
      expect_type(samples(), "character")

      expect_type(tag_group_filter_tbl(), "list")
      expect_equal(nrow(tag_group_filter_tbl()), 2)
      expect_named(tag_group_filter_tbl(), c("display", "name"))
      expect_type(clinical_group_filter_tbl(), "list")

      expect_equal(nrow(clinical_group_filter_tbl()), 1)
      expect_named(clinical_group_filter_tbl(), c("display", "name"))
      expect_type(group_filter_list(), "character")
      expect_equal(length(group_filter_list()), 3)
      expect_named(group_filter_list())

      expect_type(group_element_module_server(), "closure")
      expect_type(valid_group_filter_obj(), "list")
      expect_type(group_filter_samples(), "character")

      expect_named(feature_tbl(), c("class", "display", "feature"))
      expect_true(nrow(feature_tbl()) > 0)
      expect_named(clinical_tbl(), c("class", "display", "feature"))
      expect_equal(nrow(clinical_tbl()), 1)
      expect_type(numeric_named_list(), "list")
      expect_named(numeric_named_list())

      expect_type(numeric_element_module_server(), "closure")
      expect_type(valid_numeric_filter_obj(), "list")
      expect_type(numeric_filter_samples(), "character")

      expect_type(selected_samples(), "character")

      expect_type(output$samples_text, "character")

      filter_obj <- session$getReturned()()
      expect_type(filter_obj, "list")
      expect_named(filter_obj, c("samples", "filters"))
    }
  )
})
#
# test_that("cohort_filter_selection_server_tcga", {
#   shiny::testServer(
#     cohort_filter_selection_server,
#     args = list("dataset" = shiny::reactiveVal("TCGA")),
#     {
#       expect_type(samples(), "character")
#
#       expect_type(tag_group_filter_tbl(), "list")
#       expect_equal(nrow(tag_group_filter_tbl()), 3)
#       expect_named(tag_group_filter_tbl(), c("display", "name"))
#       expect_type(clinical_group_filter_tbl(), "list")
#
#       expect_equal(nrow(clinical_group_filter_tbl()), 3)
#       expect_named(clinical_group_filter_tbl(), c("display", "name"))
#       expect_type(group_filter_list(), "character")
#       expect_equal(length(group_filter_list()), 6)
#       expect_named(group_filter_list())
#
#       expect_type(group_element_module_server(), "closure")
#       expect_type(valid_group_filter_obj(), "list")
#       expect_type(group_filter_samples(), "character")
#
#       expect_named(feature_tbl(), c("class", "display", "feature"))
#       expect_true(nrow(feature_tbl()) > 0)
#       expect_named(clinical_tbl(), c("class", "display", "feature"))
#       expect_equal(nrow(clinical_tbl()), 3)
#       expect_type(numeric_named_list(), "list")
#       expect_named(numeric_named_list())
#
#       expect_type(numeric_element_module_server(), "closure")
#       expect_type(valid_numeric_filter_obj(), "list")
#       expect_type(numeric_filter_samples(), "character")
#
#       expect_type(selected_samples(), "character")
#
#       expect_type(output$samples_text, "character")
#
#       filter_obj <- session$getReturned()()
#       expect_type(filter_obj, "list")
#       expect_named(filter_obj, c("samples", "filters"))
#     }
#   )
# })
