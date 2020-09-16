test_that("cohort_upload_selection_server", {
  shiny::testServer(
    cohort_upload_selection_server,
    {
      session$setInputs("file1" = get_example_path("example_user_group"))
      # expect_type(user_group_tbl(), "tibble")
    }
  )
})
