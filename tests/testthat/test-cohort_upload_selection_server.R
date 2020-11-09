
test_that("cohort_upload_selection_server", {
  shiny::testServer(
    cohort_upload_selection_server,
    {
      session$setInputs("file" = get_example_path("example_user_group"))
      print(user_group_tbl())
      # expect_type(user_group_tbl(), "list")
    }
  )
})
