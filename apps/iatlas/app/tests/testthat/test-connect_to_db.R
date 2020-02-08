test_that("connect_to_db", {
    pool <- connect_to_db()
    expect_type(pool, "environment")
})
