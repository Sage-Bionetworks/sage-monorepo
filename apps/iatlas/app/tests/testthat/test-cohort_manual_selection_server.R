#TODO: figure out how to test deduped reactives: https://github.com/CRI-iAtlas/iatlas-app/issues/40
test_that("cohort_manual_selection_server", {
  shiny::testServer(
    cohort_manual_selection_server,
    {
      expect_type(default_dataset, "character")
      expect_null(selected_dataset())
    }
  )
})
