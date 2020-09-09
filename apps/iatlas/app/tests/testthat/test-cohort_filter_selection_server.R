dataset <- shiny::reactiveVal("PCAWG")
samples <- shiny::reactiveVal(iatlas.api.client::query_dataset_samples("PCAWG"))

test_that("module_works", {
  shiny::testServer(
    cohort_filter_selection_server,
    args = list("selected_dataset" = dataset, samples = samples),
    {
      expect_named(tag_named_list())
      expect_type(tag_named_list(), "character")
      expect_type(tag_element_module_server(), "closure")
      expect_type(valid_tag_filter_obj(), "list")
      expect_type(tag_filter_samples(), "list")

      expect_named(feature_named_list())
      expect_type(feature_named_list(), "list")
      expect_type(numeric_element_module_server(), "closure")
      expect_type(valid_numeric_filter_obj(), "list")
      expect_type(numeric_filter_samples(), "list")

      expect_type(selected_samples(), "list")

      expect_type(output$samples_text, "character")

      expect_type(filter_obj(), "list")

      expect_type(session$getReturned()(), "list")
    }
  )
})
