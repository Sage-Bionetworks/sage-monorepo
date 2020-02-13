test_that("perform_query", {
    tbl <- perform_query("SELECT id, display from features")
    expect_named(tbl, c("id", "display"))
})
