#TODO: figure out how to test file upload
test_that("cohort_upload_selection_server", {
  shiny::testServer(
    cohort_upload_selection_server,
    {
      expect_true(T)
    }
  )
})
