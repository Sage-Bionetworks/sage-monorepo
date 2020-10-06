test_that("data_info_server", {
  shiny::testServer(
    data_info_server,
    {
      expect_type(class_list(), "character")
      expect_type(output$classes, "list")
      session$setInputs("class_choice" = "DNA Alteration")
      expect_type(feature_tbl(), "list")
      session$setInputs("class_choice" = "All classes")
      expect_type(feature_tbl(), "list")
      session$setInputs("feature_table_rows_selected" = 1L)
      expect_type(filtered_feature_tbl(), "list")
    }
  )
})
