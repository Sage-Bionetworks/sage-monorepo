test_that("module_works", {
  shiny::testServer(
    cohort_filter_selection_server,
    args = list("dataset" = shiny::reactiveVal("PCAWG")),
    {
      expect_type(samples(), "character")

      expect_named(tag_named_list())
      expect_type(tag_named_list(), "character")
      expect_type(tag_element_module_server(), "closure")
      expect_type(valid_tag_filter_obj(), "list")
      expect_type(tag_filter_samples(), "character")

      expect_named(feature_named_list())
      expect_type(feature_named_list(), "list")
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
